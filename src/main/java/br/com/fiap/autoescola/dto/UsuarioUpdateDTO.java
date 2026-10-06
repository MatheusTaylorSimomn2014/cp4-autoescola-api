package br.com.fiap.autoescola.dto;

import br.com.fiap.autoescola.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUpdateDTO {
    @NotNull
    private Role role;
}