package br.com.fiap.autoescola.service;

import br.com.fiap.autoescola.dto.InstrucaoCancelamentoDTO;
import br.com.fiap.autoescola.dto.InstrucaoCreateDTO;
import br.com.fiap.autoescola.entity.*;
import br.com.fiap.autoescola.exception.BusinessException;
import br.com.fiap.autoescola.exception.ResourceNotFoundException;
import br.com.fiap.autoescola.mapper.InstrucaoMapper;
import br.com.fiap.autoescola.repository.AlunoRepository;
import br.com.fiap.autoescola.repository.InstrutorRepository;
import br.com.fiap.autoescola.repository.InstrucaoRepository;
import br.com.fiap.autoescola.vo.InstrucaoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class InstrucaoService {

    private static final LocalTime HORA_ABERTURA = LocalTime.of(6, 0);
    private static final LocalTime HORA_FECHAMENTO = LocalTime.of(21, 0);
    private static final long ANTECEDENCIA_MINIMA_MINUTOS = 30;
    private static final long ANTECEDENCIA_CANCELAMENTO_HORAS = 24;
    private static final int MAX_INSTRUCOES_POR_DIA = 2;

    private final InstrucaoRepository instrucaoRepository;
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;

    @Transactional
    public InstrucaoVO agendar(InstrucaoCreateDTO dto) {
        LocalDateTime dataHora = dto.getDataHora();

        // Regra: antecedência mínima de 30 minutos
        if (dataHora.isBefore(LocalDateTime.now().plusMinutes(ANTECEDENCIA_MINIMA_MINUTOS))) {
            throw new BusinessException("A instrução deve ser agendada com antecedência mínima de 30 minutos.");
        }

        // Regra: horário de funcionamento (seg-sáb, 06:00 às 21:00)
        validarHorarioFuncionamento(dataHora);

        // Regra: aluno precisa existir e estar ativo
        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(dto.getAlunoId())
                .orElseThrow(() -> new BusinessException("Aluno inexistente ou inativo."));

        // Regra: máximo 2 instruções no mesmo dia para o mesmo aluno
        LocalDateTime inicioDia = dataHora.toLocalDate().atStartOfDay();
        LocalDateTime fimDia = dataHora.toLocalDate().atTime(LocalTime.MAX);
        long agendamentosDia = instrucaoRepository.countByAlunoAndDia(aluno.getId(), inicioDia, fimDia);
        if (agendamentosDia >= MAX_INSTRUCOES_POR_DIA) {
            throw new BusinessException("O aluno já possui 2 instruções agendadas para o mesmo dia.");
        }

        // Seleciona instrutor
        Instrutor instrutor;
        if (dto.getInstrutorId() != null) {
            instrutor = instrutorRepository.findByIdAndAtivoTrue(dto.getInstrutorId())
                    .orElseThrow(() -> new BusinessException("Instrutor inexistente ou inativo."));

            // Regra: instrutor não pode ter outra instrução no mesmo horário
            List<Instrucao> conflitos = instrucaoRepository
                    .findByInstrutorAndDataHora(instrutor.getId(), dataHora);
            if (!conflitos.isEmpty()) {
                throw new BusinessException("O instrutor já possui instrução agendada nesse horário.");
            }
        } else {
            // Escolha aleatória de instrutor disponível
            List<Long> ocupados = instrucaoRepository.findInstrutoresOcupados(dataHora);
            List<Instrutor> disponiveis = instrutorRepository.findDisponiveis(ocupados);
            if (disponiveis.isEmpty()) {
                throw new BusinessException("Não há instrutores disponíveis nesse horário.");
            }
            Collections.shuffle(disponiveis, ThreadLocalRandom.current());
            instrutor = disponiveis.get(0);
        }

        Instrucao instrucao = Instrucao.builder()
                .aluno(aluno)
                .instrutor(instrutor)
                .dataHora(dataHora)
                .status(StatusInstrucao.AGENDADA)
                .build();

        return InstrucaoMapper.toVO(instrucaoRepository.save(instrucao));
    }

    @Transactional(readOnly = true)
    public List<InstrucaoVO> listar() {
        return instrucaoRepository.findAll().stream()
                .map(InstrucaoMapper::toVO)
                .toList();
    }

    @Transactional(readOnly = true)
    public InstrucaoVO buscarPorId(Long id) {
        Instrucao instrucao = instrucaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrução não encontrada."));
        return InstrucaoMapper.toVO(instrucao);
    }

    @Transactional
    public InstrucaoVO cancelar(Long id, InstrucaoCancelamentoDTO dto) {
        Instrucao instrucao = instrucaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrução não encontrada."));

        if (instrucao.getStatus() == StatusInstrucao.CANCELADA) {
            throw new BusinessException("A instrução já está cancelada.");
        }

        // Regra: cancelamento com antecedência mínima de 24 horas
        if (LocalDateTime.now().plusHours(ANTECEDENCIA_CANCELAMENTO_HORAS)
                .isAfter(instrucao.getDataHora())) {
            throw new BusinessException("A instrução só pode ser cancelada com 24h de antecedência.");
        }

        // Regra: motivo obrigatório (garantido pela validação do DTO @NotNull)

        instrucao.setStatus(StatusInstrucao.CANCELADA);
        instrucao.setMotivoCancelamento(dto.getMotivo());
        instrucao.setDataCancelamento(LocalDateTime.now());

        return InstrucaoMapper.toVO(instrucaoRepository.save(instrucao));
    }

    private void validarHorarioFuncionamento(LocalDateTime dataHora) {
        DayOfWeek dia = dataHora.getDayOfWeek();
        if (dia == DayOfWeek.SUNDAY) {
            throw new BusinessException("A autoescola não abre aos domingos.");
        }
        LocalTime hora = dataHora.toLocalTime();
        if (hora.isBefore(HORA_ABERTURA) || !hora.isBefore(HORA_FECHAMENTO)) {
            throw new BusinessException("Horário fora do funcionamento (06:00 às 21:00).");
        }
    }
}