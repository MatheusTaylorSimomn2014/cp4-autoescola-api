package com.fiap.autoescola.service;

import com.fiap.autoescola.dto.UsuarioRequestDTO;
import com.fiap.autoescola.dto.UsuarioResponseDTO;
import com.fiap.autoescola.exception.RecursoNaoEncontradoException;
import com.fiap.autoescola.exception.RegraNegocioException;
import com.fiap.autoescola.model.Usuario;
import com.fiap.autoescola.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (repository.existsByLogin(dto.getLogin())) {
            throw new RegraNegocioException("Ja existe usuario com o login informado");
        }
        Usuario usuario = Usuario.builder()
                .login(dto.getLogin())
                .senha(hashSenha(dto.getSenha()))
                .perfil(parsePerfil(dto.getPerfil()))
                .build();
        return UsuarioResponseDTO.fromEntity(repository.save(usuario));
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll().stream().map(UsuarioResponseDTO::fromEntity).toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidade(id);
        repository.findByLogin(dto.getLogin())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new RegraNegocioException("Ja existe usuario com o login informado");
                });
        usuario.setLogin(dto.getLogin());
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(hashSenha(dto.getSenha()));
        }
        usuario.setPerfil(parsePerfil(dto.getPerfil()));
        return UsuarioResponseDTO.fromEntity(repository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    Usuario buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: id " + id));
    }

    private Usuario.Perfil parsePerfil(String perfil) {
        try {
            return Usuario.Perfil.valueOf(perfil.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException("Perfil invalido. Valores aceitos: "
                    + Arrays.toString(Usuario.Perfil.values()));
        }
    }

    /** Hash simples SHA-256 em hex (didatico; em producao usar BCrypt). */
    private String hashSenha(String senha) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(senha.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
