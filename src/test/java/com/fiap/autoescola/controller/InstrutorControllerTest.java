package com.fiap.autoescola.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.autoescola.client.ViaCepClient;
import com.fiap.autoescola.client.ViaCepResponse;
import com.fiap.autoescola.dto.EnderecoDTO;
import com.fiap.autoescola.dto.InstrutorRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integracao (CRUD completo) do recurso de Instrutores.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InstrutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ViaCepClient viaCepClient;

    private InstrutorRequestDTO novoInstrutor(String cpf, String cnh) {
        return InstrutorRequestDTO.builder()
                .nome("Carlos Eduardo")
                .cpf(cpf)
                .cnh(cnh)
                .dataNascimento(LocalDate.of(1985, 5, 10))
                .endereco(EnderecoDTO.builder().cep("04538-133").build())
                .build();
    }

    private void mockCep() {
        given(viaCepClient.consultarCep(any())).willReturn(ViaCepResponse.builder()
                .cep("04538133").logradouro("Rua Funchal").bairro("Vila Olimpia")
                .localidade("Sao Paulo").uf("SP").build());
    }

    @Test
    void deveCriarInstrutorComCreatedELocation() throws Exception {
        mockCep();
        mockMvc.perform(post("/api/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoInstrutor("222.333.444-01", "CNH-001"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Carlos Eduardo"))
                .andExpect(jsonPath("$.cnh").value("CNH-001"))
                .andExpect(jsonPath("$.endereco.uf").value("SP"));
    }

    @Test
    void deveListarBuscarAtualizarERemoverInstrutor() throws Exception {
        mockCep();
        String corpo = mockMvc.perform(post("/api/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoInstrutor("222.333.444-02", "CNH-002"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(corpo).get("id").asLong();

        mockMvc.perform(get("/api/instrutores/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cnh").value("CNH-002"));

        mockMvc.perform(get("/api/instrutores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());

        InstrutorRequestDTO update = novoInstrutor("222.333.444-02", "CNH-002");
        update.setNome("Carlos E. Machado");
        mockMvc.perform(put("/api/instrutores/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos E. Machado"));

        mockMvc.perform(delete("/api/instrutores/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/instrutores/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarCnhDuplicada() throws Exception {
        mockCep();
        String json = objectMapper.writeValueAsString(novoInstrutor("222.333.444-03", "CNH-003"));
        mockMvc.perform(post("/api/instrutores").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        // mesmo CNH, CPF diferente -> conflito de unicidade
        json = objectMapper.writeValueAsString(novoInstrutor("222.333.444-99", "CNH-003"));
        mockMvc.perform(post("/api/instrutores").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        mockCep();
        InstrutorRequestDTO invalido = novoInstrutor("222.333.444-04", "CNH-004");
        invalido.setDataNascimento(LocalDate.now().plusYears(1)); // data futura
        mockMvc.perform(post("/api/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
}
