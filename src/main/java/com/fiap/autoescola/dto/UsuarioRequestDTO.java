package com.fiap.autoescola.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload para criacao/atualizacao de usuario")
public class UsuarioRequestDTO {

    @NotBlank(message = "Login e obrigatorio")
    @Size(min = 3, max = 60)
    @Schema(example = "maria.silva")
    private String login;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 6, max = 60)
    @Schema(example = "senha123")
    private String senha;

    @NotBlank(message = "Perfil e obrigatorio (ADMIN, INSTRUTOR ou ALUNO)")
    @Schema(example = "ALUNO")
    private String perfil;
}
