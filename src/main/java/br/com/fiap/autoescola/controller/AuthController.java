package br.com.fiap.autoescola.controller;

import br.com.fiap.autoescola.dto.LoginDTO;
import br.com.fiap.autoescola.dto.TokenDTO;
import br.com.fiap.autoescola.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody LoginDTO dto) {
        return authService.autenticar(dto);
    }
}