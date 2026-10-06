package com.fiap.autoescola.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.autoescola.client.ViaCepClient;
import com.fiap.autoescola.client.ViaCepResponse;
import com.fiap.autoescola.dto.AlunoRequestDTO;
import com.fiap.autoescola.dto.EnderecoDTO;
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
 * Testes de integracao (CRUD completo) do recurso de Alunos.
 * O cliente ViaCEP e mockado para os testes nao dependerem da rede.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ViaCepClient viaCepClient;

    private AlunoRequestDTO novoAluno(String cpf) {
        return AlunoRequestDTO.builder()
                .nome("Maria da Silva")
                .cpf(cpf)
                .dataNascimento(LocalDate.of(2000, 1, 15))
                .endereco(EnderecoDTO.builder().cep("01310-100").build())
                .build();
    }

    private void mockCep() {
        given(viaCepClient.consultarCep(any())).willReturn(ViaCepResponse.builder()
                .cep("01310100").logradouro("Avenida Paulista").bairro("Bela Vista")
                .localidade("Sao Paulo").uf("SP").build());
    }

    @Test
    void deveCriarAlunoComCreatedELocation() throws Exception {
        mockCep();
        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoAluno("111.222.333-01"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.status").value("A_BAURA"))
                .andExpect(jsonPath("$.endereco.cidade").value("Sao Paulo"));
    }

    @Test
    void deveListarBuscarAtualizarERemoverAluno() throws Exception {
        mockCep();
        String corpo = mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoAluno("111.222.333-02"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(corpo).get("id").asLong();

        // GET by id
        mockMvc.perform(get("/api/alunos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("111.222.333-02"));

        // GET lista
        mockMvc.perform(get("/api/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());

        // PUT atualizacao
        AlunoRequestDTO update = novoAluno("111.222.333-02");
        update.setNome("Maria Souza");
        mockMvc.perform(put("/api/alunos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Souza"));

        // DELETE
        mockMvc.perform(delete("/api/alunos/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/alunos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarCpfDuplicado() throws Exception {
        mockCep();
        String json = objectMapper.writeValueAsString(novoAluno("111.222.333-03"));
        mockMvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarPayloadInvalido() throws Exception {
        mockCep();
        AlunoRequestDTO invalido = novoAluno("cpf-invalido");
        invalido.setNome(null);
        mockMvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
}
