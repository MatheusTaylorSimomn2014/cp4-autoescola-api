package br.com.fiap.autoescola.mapper;

import br.com.fiap.autoescola.dto.AlunoCreateDTO;
import br.com.fiap.autoescola.dto.AlunoUpdateDTO;
import br.com.fiap.autoescola.dto.EnderecoDTO;
import br.com.fiap.autoescola.entity.Aluno;
import br.com.fiap.autoescola.entity.Endereco;
import br.com.fiap.autoescola.vo.AlunoListVO;
import br.com.fiap.autoescola.vo.AlunoVO;
import br.com.fiap.autoescola.vo.EnderecoVO;

public class AlunoMapper {
    public static Aluno toEntity(AlunoCreateDTO dto) {
        return Aluno.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .cpf(dto.getCpf())
                .endereco(toEndereco(dto.getEndereco()))
                .build();
    }

    public static void updateEntity(Aluno entity, AlunoUpdateDTO dto) {
        entity.setNome(dto.getNome());
        entity.setTelefone(dto.getTelefone());
        entity.setEndereco(toEndereco(dto.getEndereco()));
    }

    public static AlunoVO toVO(Aluno entity) {
        return AlunoVO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .telefone(entity.getTelefone())
                .cpf(entity.getCpf())
                .endereco(toEnderecoVO(entity.getEndereco()))
                .build();
    }

    public static AlunoListVO toListVO(Aluno entity) {
        return AlunoListVO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .cpf(entity.getCpf())
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