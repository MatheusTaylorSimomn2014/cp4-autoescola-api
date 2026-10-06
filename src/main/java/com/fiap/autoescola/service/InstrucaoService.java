package com.fiap.autoescola.service;

import com.fiap.autoescola.dto.InstrucaoRequestDTO;
import com.fiap.autoescola.dto.InstrucaoResponseDTO;
import com.fiap.autoescola.exception.RecursoNaoEncontradoException;
import com.fiap.autoescola.exception.RegraNegocioException;
import com.fiap.autoescola.model.*;
import com.fiap.autoescola.repository.InstrucaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Servico de agendamento e cancelamento de instrucoes (aulas praticas).
 *
 * Regras de negocio implementadas (adaptadas do documento da atividade):
 *  R1 - A instrucao deve ter inicio anterior ao termino; duracao maxima de 4 horas.
 *  R2 - Agendamentos somente em horario comercial (dias uteis, entre 08h e 18h).
 *  R3 - Nao e permitido sobrepor horarios do mesmo instrutor (conflito de agenda).
 *  R4 - O aluno deve estar com processo ativo (A_BAURA ou EM_ANDAMENTO).
 *  R5 - Agendamento com antecedencia minima de 24 horas em relacao ao inicio.
 *  R6 - Cancelamento apenas para instrucoes AGENDADAS e com antecedencia minima
 *       de 24 horas; instrucoes canceladas com frequencia geram bloqueio
 *       (limite configuravel de cancelamentos por aluno).
 */
@Service
public class InstrucaoService {

    private static final LocalTime ABERTURA = LocalTime.of(8, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(18, 0);
    private static final Duration DURACAO_MAXIMA = Duration.ofHours(4);
    private static final Duration ANTECEDENCIA_MINIMA = Duration.ofHours(24);

    private final InstrucaoRepository repository;
    private final AlunoService alunoService;
    private final InstrutorService instrutorService;

    @Value("${app.regra.limite-cancelamentos:3}")
    private int limiteCancelamentos;

    public InstrucaoService(InstrucaoRepository repository,
                            AlunoService alunoService,
                            InstrutorService instrutorService) {
        this.repository = repository;
        this.alunoService = alunoService;
        this.instrutorService = instrutorService;
    }

    @Transactional
    public InstrucaoResponseDTO agendar(InstrucaoRequestDTO dto) {
        Aluno aluno = alunoService.buscarEntidade(dto.getAlunoId());
        Instrutor instrutor = instrutorService.buscarEntidade(dto.getInstrutorId());

        LocalDateTime inicio = dto.getDataHoraInicio();
        LocalDateTime fim = dto.getDataHoraFim();

        // R1: intervalo valido e duracao maxima
        if (!inicio.isBefore(fim)) {
            throw new RegraNegocioException("A data/hora de inicio deve ser anterior a de termino");
        }
        if (Duration.between(inicio, fim).compareTo(DURACAO_MAXIMA) > 0) {
            throw new RegraNegocioException("Duracao maxima de uma instrucao e de 4 horas");
        }

        // R5: antecedencia minima de 24 horas
        if (inicio.isBefore(LocalDateTime.now().plus(ANTECEDENCIA_MINIMA))) {
            throw new RegraNegocioException("O agendamento deve ser feito com antecedencia minima de 24 horas");
        }

        // R2: dias uteis e horario comercial
        if (inicio.getDayOfWeek() == DayOfWeek.SATURDAY || inicio.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RegraNegocioException("Instrucoes so podem ser agendadas em dias uteis");
        }
        if (inicio.toLocalTime().isBefore(ABERTURA) || fim.toLocalTime().isAfter(FECHAMENTO)
                || !inicio.toLocalDate().equals(fim.toLocalDate())) {
            throw new RegraNegocioException("Instrucoes devem ocorrer dentro do horario comercial (08h as 18h), no mesmo dia");
        }

        // R4: processo do aluno ativo
        if (aluno.getStatus() == StatusProcesso.CANCELADO || aluno.getStatus() == StatusProcesso.CONCLUIDO) {
            throw new RegraNegocioException("Aluno com processo " + aluno.getStatus() + " nao pode agendar instrucoes");
        }

        // R6: limite de cancelamentos pelo aluno
        long canceladasPeloAluno = repository.countByAlunoAndStatusIn(aluno.getId(),
                List.of(StatusInstrucao.CANCELADA));
        if (canceladasPeloAluno >= limiteCancelamentos) {
            throw new RegraNegocioException("Aluno atingiu o limite de " + limiteCancelamentos
                    + " cancelamentos e esta bloqueado de novos agendamentos");
        }

        // R3: conflito de agenda do instrutor
        List<Instrucao> conflitos = repository.findConflitosInstrutor(instrutor.getId(), inicio, fim);
        if (!conflitos.isEmpty()) {
            throw new RegraNegocioException("Instrutor ja possui instrucao agendada no periodo informado (conflito de agenda)");
        }

        Instrucao instrucao = Instrucao.builder()
                .aluno(aluno)
                .instrutor(instrutor)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .categoria(dto.getCategoria() == null ? "B" : dto.getCategoria())
                .observacao(dto.getObservacao())
                .status(StatusInstrucao.AGENDADA)
                .criadoEm(LocalDateTime.now())
                .build();

        Instrucao salvo = repository.save(instrucao);

        // Primeiro agendamento move o processo do aluno para EM_ANDAMENTO
        if (aluno.getStatus() == StatusProcesso.A_BAURA) {
            aluno.setStatus(StatusProcesso.EM_ANDAMENTO);
        }

        return InstrucaoResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public InstrucaoResponseDTO cancelar(Long id, String motivo) {
        Instrucao instrucao = repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Instrucao nao encontrada: id " + id));

        // R6: somente instrucoes AGENDADAS podem ser canceladas
        if (instrucao.getStatus() != StatusInstrucao.AGENDADA) {
            throw new RegraNegocioException("Somente instrucoes com status AGENDADA podem ser canceladas");
        }

        // R6: antecedencia minima de 24 horas para cancelamento
        if (LocalDateTime.now().plus(ANTECEDENCIA_MINIMA).isAfter(instrucao.getDataHoraInicio())) {
            throw new RegraNegocioException("O cancelamento deve ser feito com antecedencia minima de 24 horas");
        }

        instrucao.setStatus(StatusInstrucao.CANCELADA);
        String nota = "CANCELADA: " + (motivo == null || motivo.isBlank() ? "sem motivo informado" : motivo);
        instrucao.setObservacao(instrucao.getObservacao() == null
                ? nota : instrucao.getObservacao() + " | " + nota);

        return InstrucaoResponseDTO.fromEntity(repository.save(instrucao));
    }

    @Transactional
    public InstrucaoResponseDTO concluir(Long id) {
        Instrucao instrucao = repository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Instrucao nao encontrada: id " + id));
        if (instrucao.getStatus() != StatusInstrucao.AGENDADA
                && instrucao.getStatus() != StatusInstrucao.EM_ANDAMENTO) {
            throw new RegraNegocioException("Somente instrucoes AGENDADA ou EM_ANDAMENTO podem ser concluidas");
        }
        instrucao.setStatus(StatusInstrucao.CONCLUIDA);
        return InstrucaoResponseDTO.fromEntity(repository.save(instrucao));
    }

    public List<InstrucaoResponseDTO> listarTodas() {
        return repository.findAll().stream().map(InstrucaoResponseDTO::fromEntity).toList();
    }

    public List<InstrucaoResponseDTO> listarPorAluno(Long alunoId) {
        alunoService.buscarEntidade(alunoId);
        return repository.findByAlunoId(alunoId).stream().map(InstrucaoResponseDTO::fromEntity).toList();
    }

    public List<InstrucaoResponseDTO> listarPorInstrutor(Long instrutorId) {
        instrutorService.buscarEntidade(instrutorId);
        return repository.findByInstrutorId(instrutorId).stream().map(InstrucaoResponseDTO::fromEntity).toList();
    }

    public InstrucaoResponseDTO buscarPorId(Long id) {
        return InstrucaoResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Instrucao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instrucao nao encontrada: id " + id));
    }
}
