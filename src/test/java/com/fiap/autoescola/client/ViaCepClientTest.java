package com.fiap.autoescola.client;

import com.fiap.autoescola.exception.ServicoExternoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Teste unitario do cliente da API externa ViaCEP usando
 * MockRestServiceServer (sem dependencia de rede).
 */
class ViaCepClientTest {

    private MockRestServiceServer server;
    private ViaCepClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ViaCepClient(builder, "https://viacep.com.br/ws");
    }

    @Test
    void deveConsultarCepComSucesso() {
        String json = """
                {
                  "cep": "01310-100",
                  "logradouro": "Avenida Paulista",
                  "complemento": "de 612 a 1510 - lado par",
                  "bairro": "Bela Vista",
                  "localidade": "Sao Paulo",
                  "uf": "SP"
                }
                """;
        server.expect(requestTo("https://viacep.com.br/ws/01310100/json/"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ViaCepResponse response = client.consultarCep("01310-100");

        assertEquals("Avenida Paulista", response.getLogradouro());
        assertEquals("Sao Paulo", response.getLocalidade());
        assertEquals("SP", response.getUf());
        server.verify();
    }

    @Test
    void deveLancarExcecaoParaCepInexistente() {
        server.expect(requestTo("https://viacep.com.br/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": \"true\"}", MediaType.APPLICATION_JSON));

        ServicoExternoException ex = assertThrows(ServicoExternoException.class,
                () -> client.consultarCep("99999-999"));
        assertTrue(ex.getMessage().contains("nao encontrado"));
    }

    @Test
    void deveLancarExcecaoQuandoServicoExternoEstaIndisponivel() {
        server.expect(requestTo("https://viacep.com.br/ws/01310100/json/"))
                .andRespond(withServerError());

        assertThrows(ServicoExternoException.class, () -> client.consultarCep("01310-100"));
    }

    @Test
    void deveRejeitarCepComFormatoInvalidoSemChamarAApi() {
        assertThrows(ServicoExternoException.class, () -> client.consultarCep("123"));
        server.verify(); // nenhuma chamada esperada/realizada
    }
}
