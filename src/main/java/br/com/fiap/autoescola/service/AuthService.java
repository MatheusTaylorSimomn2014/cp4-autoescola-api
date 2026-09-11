package br.com.fiap.autoescola.service;

import br.com.fiap.autoescola.dto.LoginDTO;
import br.com.fiap.autoescola.dto.TokenDTO;
import br.com.fiap.autoescola.entity.Usuario;
import br.com.fiap.autoescola.repository.UsuarioRepository;
import br.com.fiap.autoescola.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public TokenDTO autenticar(LoginDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.gerarToken(userDetails);

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        return TokenDTO.builder()
                .token(token)
                .tipo("Bearer")
                .username(usuario.getUsername())
                .role(usuario.getRole().name())
                .build();
    }
}