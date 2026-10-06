package br.com.fiap.autoescola.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}