package br.com.fiap.autoescola.config;

import br.com.fiap.autoescola.entity.Role;
import br.com.fiap.autoescola.entity.Usuario;
import br.com.fiap.autoescola.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-default.username}")
    private String adminUsername;

    @Value("${app.admin-default.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByUsername(adminUsername).isEmpty()) {
            Usuario admin = Usuario.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            usuarioRepository.save(admin);
            log.info("Usuário administrador padrão criado: {} / {}", adminUsername, adminPassword);
        }
    }
}