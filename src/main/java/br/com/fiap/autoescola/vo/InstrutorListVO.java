package br.com.fiap.autoescola.vo;

import br.com.fiap.autoescola.entity.Especialidade;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrutorListVO {
    private Long id;
    private String nome;
    private String email;
    private String cnh;
    private Especialidade especialidade;
}