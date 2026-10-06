package br.com.fiap.autoescola.mapper;

import br.com.fiap.autoescola.dto.EnderecoDTO;
import br.com.fiap.autoescola.dto.InstrutorCreateDTO;
import br.com.fiap.autoescola.dto.InstrutorUpdateDTO;
import br.com.fiap.autoescola.entity.Endereco;
import br.com.fiap.autoescola.entity.Instrutor;
import br.com.fiap.autoescola.vo.EnderecoVO;
import br.com.fiap.autoescola.vo.InstrutorListVO;
import br.com.fiap.autoescola.vo.InstrutorVO;

public class InstrutorMapper {
    public static Instrutor toEntity(InstrutorCreateDTO dto) {
        return Instrutor.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .cnh(dto.getCnh())
                .especialidade(dto.getEspecialidade())
                .endereco(toEndereco(dto.getEndereco()))
                .ativo(true)
                .build();
    }

    public static void updateEntity(Instrutor entity, InstrutorUpdateDTO dto) {
        entity.setNome(dto.getNome());
        entity.setTelefone(dto.getTelefone());
        entity.setEndereco(toEndereco(dto.getEndereco()));
    }

    public static InstrutorVO toVO(Instrutor entity) {
        return InstrutorVO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .telefone(entity.getTelefone())
                .cnh(entity.getCnh())
                .especialidade(entity.getEspecialidade())
                .endereco(toEnderecoVO(entity.getEndereco()))
                .ativo(entity.getAtivo())
                .build();
    }

    public static InstrutorListVO toListVO(Instrutor entity) {
        return InstrutorListVO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .cnh(entity.getCnh())
                .especialidade(entity.getEspecialidade())
                .build();
    }

    private static Endereco toEndereco(EnderecoDTO dto) {
        return Endereco.builder()
                .logradouro(dto.getLogradouro())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .uf(dto.getUf())
                .cep(dto.getCep())
                .build();
    }

    private static EnderecoVO toEnderecoVO(Endereco entity) {
        return EnderecoVO.builder()
                .logradouro(entity.getLogradouro())
                .numero(entity.getNumero())
                .complemento(entity.getComplemento())
                .bairro(entity.getBairro())
                .cidade(entity.getCidade())
                .uf(entity.getUf())
                .cep(entity.getCep())
                .build();
    }
}