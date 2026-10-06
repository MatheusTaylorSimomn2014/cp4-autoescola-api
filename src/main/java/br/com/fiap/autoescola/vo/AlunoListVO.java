package br.com.fiap.autoescola.vo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoListVO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
}