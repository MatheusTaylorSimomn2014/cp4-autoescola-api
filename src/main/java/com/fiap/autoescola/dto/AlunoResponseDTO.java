package com.fiap.autoescola.dto;

import com.fiap.autoescola.model.Aluno;
import com.fiap.autoescola.model.StatusProcesso;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private StatusProcesso status;
    private EnderecoDTO endereco;

    public static AlunoResponseDTO fromEntity(Aluno a) {
        return AlunoResponseDTO.builder()
                .id(a.getId())
                .nome(a.getNome())
                .cpf(a.getCpf())
                .dataNascimento(a.getDataNascimento())
                .status(a.getStatus())
                .endereco(a.getEndereco() == null ? null : EnderecoDTO.builder()
                        .cep(a.getEndereco().getCep())
                        .logradouro(a.getEndereco().getLogradouro())
                        .numero(a.getEndereco().getNumero())
                        .complemento(a.getEndereco().getComplemento())
                        .bairro(a.getEndereco().getBairro())
                        .cidade(a.getEndereco().getCidade())
                        .uf(a.getEndereco().getUf())
                        .build())
                .build();
    }
}
