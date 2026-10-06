package com.fiap.autoescola.service;

import com.fiap.autoescola.dto.AlunoRequestDTO;
import com.fiap.autoescola.dto.AlunoResponseDTO;
import com.fiap.autoescola.exception.RecursoNaoEncontradoException;
import com.fiap.autoescola.exception.RegraNegocioException;
import com.fiap.autoescola.model.Aluno;
import com.fiap.autoescola.repository.AlunoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository repository;
    private final EnderecoService enderecoService;

    public AlunoService(AlunoRepository repository, EnderecoService enderecoService) {
        this.repository = repository;
        this.enderecoService = enderecoService;
    }

    @Transactional
    public AlunoResponseDTO criar(AlunoRequestDTO dto) {
        validarUnicidade(dto, null);
        Aluno aluno = Aluno.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .dataNascimento(dto.getDataNascimento())
                .endereco(enderecoService.montarEndereco(dto.getEndereco()))
                .build();
        return AlunoResponseDTO.fromEntity(repository.save(aluno));
    }

    public List<AlunoResponseDTO> listarTodos() {
        return repository.findAll().stream().map(AlunoResponseDTO::fromEntity).toList();
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        return AlunoResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional
    public AlunoResponseDTO atualizar(Long id, AlunoRequestDTO dto) {
        Aluno aluno = buscarEntidade(id);
        validarUnicidade(dto, id);
        aluno.setNome(dto.getNome());
        aluno.setCpf(dto.getCpf());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setEndereco(enderecoService.montarEndereco(dto.getEndereco()));
        return AlunoResponseDTO.fromEntity(repository.save(aluno));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    Aluno buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno nao encontrado: id " + id));
    }

    private void validarUnicidade(AlunoRequestDTO dto, Long idAtual) {
        repository.findByCpf(dto.getCpf())
                .filter(e -> !e.getId().equals(idAtual))
                .ifPresent(e -> {
                    throw new RegraNegocioException("Ja existe aluno com o CPF informado");
                });
    }
}
