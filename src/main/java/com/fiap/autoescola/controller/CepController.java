package com.fiap.autoescola.controller;

import com.fiap.autoescola.client.ViaCepClient;
import com.fiap.autoescola.client.ViaCepResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint que expoe o consumo da API externa ViaCEP para o frontend
 * autocompletar o endereco a partir do CEP informado.
 */
@RestController
@RequestMapping("/api/cep")
@Tag(name = "Integracao Externa", description = "Consumo da API publica ViaCEP")
public class CepController {

    private final ViaCepClient viaCepClient;

    public CepController(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    @Operation(summary = "Consulta um CEP na API externa ViaCEP e retorna os dados do endereco")
    @GetMapping("/{cep}")
    public ViaCepResponse consultarCep(@PathVariable String cep) {
        return viaCepClient.consultarCep(cep);
    }
}
