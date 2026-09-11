package br.com.fiap.autoescola.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {
    private String token;
    private String tipo;
    private String username;
    private String role;
}