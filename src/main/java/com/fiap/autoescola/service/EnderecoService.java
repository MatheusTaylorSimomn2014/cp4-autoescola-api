package com.fiap.autoescola.service;

import com.fiap.autoescola.client.ViaCepClient;
import com.fiap.autoescola.dto.EnderecoDTO;
import com.fiap.autoescola.model.Endereco;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Enriquecimento de endereco a partir do CEP consultando a API externa ViaCEP.
 * Se o frontend ja informou os demais campos, o input manual e preservado;
 * caso contrario, os dados vem da consulta externa.
 */
@Service
public class EnderecoService {

    private final ViaCepClient viaCepClient;

    public EnderecoService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public Endereco montarEndereco(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }

        Endereco endereco = new Endereco();
        endereco.setCep(dto.getCep());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());

        boolean precisaConsultar = !StringUtils.hasText(dto.getLogradouro())
                || !StringUtils.hasText(dto.getBairro())
                || !StringUtils.hasText(dto.getCidade())
                || !StringUtils.hasText(dto.getUf());

        if (precisaConsultar && StringUtils.hasText(dto.getCep())) {
            var cep = viaCepClient.consultarCep(dto.getCep());
            endereco.setLogradouro(defaultIfBlank(dto.getLogradouro(), cep.getLogradouro()));
            endereco.setBairro(defaultIfBlank(dto.getBairro(), cep.getBairro()));
            endereco.setCidade(defaultIfBlank(dto.getCidade(), cep.getLocalidade()));
            endereco.setUf(defaultIfBlank(dto.getUf(), cep.getUf()));
            if (!StringUtils.hasText(endereco.getComplemento())) {
                endereco.setComplemento(cep.getComplemento());
            }
        } else {
            endereco.setLogradouro(dto.getLogradouro());
            endereco.setBairro(dto.getBairro());
            endereco.setCidade(dto.getCidade());
            endereco.setUf(dto.getUf());
        }

        return endereco;
    }

    private String defaultIfBlank(String valor, String padrao) {
        return StringUtils.hasText(valor) ? valor : padrao;
    }
}
