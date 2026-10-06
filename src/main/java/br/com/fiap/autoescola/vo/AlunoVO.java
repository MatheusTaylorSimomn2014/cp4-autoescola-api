package br.com.fiap.autoescola.vo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoVO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private EnderecoVO endereco;
}