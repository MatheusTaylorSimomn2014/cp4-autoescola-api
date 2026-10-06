package br.com.fiap.autoescola.service;

import br.com.fiap.autoescola.dto.AlterarSenhaDTO;
import br.com.fiap.autoescola.dto.UsuarioCreateDTO;
import br.com.fiap.autoescola.dto.UsuarioUpdateDTO;
import br.com.fiap.autoescola.entity.Usuario;
import br.com.fiap.autoescola.exception.BusinessException;
import br.com.fiap.autoescola.exception.ResourceNotFoundException;
import br.com.fiap.autoescola.mapper.UsuarioMapper;
import br.com.fiap.autoescola.repository.UsuarioRepository;
import br.com.fiap.autoescola.vo.UsuarioVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioVO criar(UsuarioCreateDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("Username já cadastrado.");
        }
        String senhaCriptografada = passwordEncoder.encode(dto.getPassword());
        Usuario usuario = UsuarioMapper.toEntity(dto, senhaCriptografada);
        return UsuarioMapper.toVO(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioVO> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toVO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioVO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        return UsuarioMapper.toVO(usuario);
    }

    @Transactional
    public UsuarioVO atualizarPerfil(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        usuario.setRole(dto.getRole());
        return UsuarioMapper.toVO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public void alterarPropriaSenha(String username, AlterarSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getPassword())) {
            throw new BusinessException("Senha atual incorreta.");
        }
        if (passwordEncoder.matches(dto.getNovaSenha(), usuario.getPassword())) {
            throw new BusinessException("A nova senha deve ser diferente da atual.");
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
    }
}