package br.com.fiap.autoescola.vo;

import br.com.fiap.autoescola.entity.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioVO {
    private Long id;
    private String username;
    private Role role;
}