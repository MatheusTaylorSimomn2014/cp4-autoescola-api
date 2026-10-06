package com.fiap.autoescola.client;

import com.fiap.autoescola.exception.ServicoExternoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente para consumo da API publica ViaCEP (https://viacep.com.br).
 *
 * Demonstra a integracao com um servico externo via HTTP/JSON usando
 * o RestClient do Spring Framework 6 (Spring Boot 3.x).
 */
@Component
public class ViaCepClient {

    private static final Logger log = LoggerFactory.getLogger(ViaCepClient.class);

    private final RestClient restClient;

    public ViaCepClient(RestClient.Builder builder,
                        @Value("${app.api.viacep.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    /**
     * Consulta um CEP na API externa.
     *
     * @param cep CEP no formato 00000000 ou 00000-000
     * @return resposta do ViaCEP
     * @throws ServicoExternoException quando a API esta indisponivel ou retorna erro
     */
    public ViaCepResponse consultarCep(String cep) {
        String cepLimpo = cep == null ? "" : cep.replaceAll("\\D", "");
        if (cepLimpo.length() != 8) {
            throw new ServicoExternoException("CEP invalido: " + cep);
        }

        try {
            log.info("Consultando CEP {} na API ViaCEP", cepLimpo);
            ViaCepResponse response = restClient.get()
                    .uri("/{cep}/json/", cepLimpo)
                    .retrieve()
                    .body(ViaCepResponse.class);

            if (response == null || response.isNotFound()) {
                throw new ServicoExternoException("CEP nao encontrado: " + cep);
            }
            return response;
        } catch (RestClientException ex) {
            throw new ServicoExternoException(
                    "Falha ao consultar a API ViaCEP para o CEP " + cep + ": " + ex.getMessage(), ex);
        }
    }
}
