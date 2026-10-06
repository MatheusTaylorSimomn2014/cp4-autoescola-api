package com.fiap.autoescola.dto;

import com.fiap.autoescola.model.Usuario;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String login;
    private Usuario.Perfil perfil;

    public static UsuarioResponseDTO fromEntity(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .login(u.getLogin())
                .perfil(u.getPerfil())
                .build();
    }
}
