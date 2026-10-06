package com.fiap.autoescola.dto;

import com.fiap.autoescola.model.Instrutor;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrutorResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String cnh;
    private LocalDate dataNascimento;
    private EnderecoDTO endereco;

    public static InstrutorResponseDTO fromEntity(Instrutor i) {
        return InstrutorResponseDTO.builder()
                .id(i.getId())
                .nome(i.getNome())
                .cpf(i.getCpf())
                .cnh(i.getCnh())
                .dataNascimento(i.getDataNascimento())
                .endereco(i.getEndereco() == null ? null : EnderecoDTO.builder()
                        .cep(i.getEndereco().getCep())
                        .logradouro(i.getEndereco().getLogradouro())
                        .numero(i.getEndereco().getNumero())
                        .complemento(i.getEndereco().getComplemento())
                        .bairro(i.getEndereco().getBairro())
                        .cidade(i.getEndereco().getCidade())
                        .uf(i.getEndereco().getUf())
                        .build())
                .build();
    }
}
