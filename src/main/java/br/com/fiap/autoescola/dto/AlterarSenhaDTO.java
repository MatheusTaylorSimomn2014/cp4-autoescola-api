package br.com.fiap.autoescola.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlterarSenhaDTO {
    @NotBlank
    private String senhaAtual;

    @NotBlank
    @Size(min = 6, max = 100)
    private String novaSenha;
}