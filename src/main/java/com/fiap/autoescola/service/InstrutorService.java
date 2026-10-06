package com.fiap.autoescola.service;

import com.fiap.autoescola.dto.InstrutorRequestDTO;
import com.fiap.autoescola.dto.InstrutorResponseDTO;
import com.fiap.autoescola.exception.RecursoNaoEncontradoException;
import com.fiap.autoescola.exception.RegraNegocioException;
import com.fiap.autoescola.model.Instrutor;
import com.fiap.autoescola.repository.InstrutorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstrutorService {

    private final InstrutorRepository repository;
    private final EnderecoService enderecoService;

    public InstrutorService(InstrutorRepository repository, EnderecoService enderecoService) {
        this.repository = repository;
        this.enderecoService = enderecoService;
    }

    @Transactional
    public InstrutorResponseDTO criar(InstrutorRequestDTO dto) {
        validarUnicidade(dto, null);
        Instrutor instrutor = Instrutor.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .cnh(dto.getCnh())
                .dataNascimento(dto.getDataNascimento())
                .endereco(enderecoService.montarEndereco(dto.getEndereco()))
                .build();
        return InstrutorResponseDTO.fromEntity(repository.save(instrutor));
    }

    public List<InstrutorResponseDTO> listarTodos() {
        return repository.findAll().stream().map(InstrutorResponseDTO::fromEntity).toList();
    }

    public InstrutorResponseDTO buscarPorId(Long id) {
        return InstrutorResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional
    public InstrutorResponseDTO atualizar(Long id, InstrutorRequestDTO dto) {
        Instrutor instrutor = buscarEntidade(id);
        validarUnicidade(dto, id);
        instrutor.setNome(dto.getNome());
        instrutor.setCpf(dto.getCpf());
        instrutor.setCnh(dto.getCnh());
        instrutor.setDataNascimento(dto.getDataNascimento());
        instrutor.setEndereco(enderecoService.montarEndereco(dto.getEndereco()));
        return InstrutorResponseDTO.fromEntity(repository.save(instrutor));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    Instrutor buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instrutor nao encontrado: id " + id));
    }

    private void validarUnicidade(InstrutorRequestDTO dto, Long idAtual) {
        repository.findByCpf(dto.getCpf())
                .filter(e -> !e.getId().equals(idAtual))
                .ifPresent(e -> {
                    throw new RegraNegocioException("Ja existe instrutor com o CPF informado");
                });
        repository.findByCnh(dto.getCnh())
                .filter(e -> !e.getId().equals(idAtual))
                .ifPresent(e -> {
                    throw new RegraNegocioException("Ja existe instrutor com a CNH informada");
                });
    }
}
