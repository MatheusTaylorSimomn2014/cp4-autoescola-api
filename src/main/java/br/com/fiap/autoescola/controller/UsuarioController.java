package br.com.fiap.autoescola.controller;

import br.com.fiap.autoescola.dto.AlterarSenhaDTO;
import br.com.fiap.autoescola.dto.UsuarioCreateDTO;
import br.com.fiap.autoescola.dto.UsuarioUpdateDTO;
import br.com.fiap.autoescola.service.UsuarioService;
import br.com.fiap.autoescola.vo.UsuarioVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ADMIN
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioVO criar(@Valid @RequestBody UsuarioCreateDTO dto) {
        return usuarioService.criar(dto);
    }

    // ADMIN
    @GetMapping
    public List<UsuarioVO> listar() {
        return usuarioService.listar();
    }

    // ADMIN
    @GetMapping("/{id}")
    public UsuarioVO buscar(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    // ADMIN – atualiza o perfil (role)
    @PutMapping("/{id}/perfil")
    public UsuarioVO atualizarPerfil(@PathVariable Long id,
                                     @Valid @RequestBody UsuarioUpdateDTO dto) {
        return usuarioService.atualizarPerfil(id, dto);
    }

    // ADMIN
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
    }

    // Qualquer usuário autenticado altera a própria senha
    @PutMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarPropriaSenha(@Valid @RequestBody AlterarSenhaDTO dto,
                                    Authentication authentication) {
        usuarioService.alterarPropriaSenha(authentication.getName(), dto);
    }
}