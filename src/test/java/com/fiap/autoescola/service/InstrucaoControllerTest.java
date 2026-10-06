package com.fiap.autoescola.service;

import com.fiap.autoescola.client.ViaCepClient;
import com.fiap.autoescola.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integracao do agendamento/cancelamento de instrucoes,
 * cobrindo as regras de negocio R1..R6.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InstrucaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private InstrutorService instrutorService;

    @MockBean
    private ViaCepClient viaCepClient;

    /** Proxima data util (seg..sex) 10h-11h com pelo menos 48h de antecedencia. */
    private LocalDateTime[] proximaJanelaUtil(int horaInicio) {
        LocalDateTime inicio = LocalDateTime.now().plusDays(2).withHour(horaInicio).withMinute(0).withSecond(0).withNano(0);
        while (inicio.getDayOfWeek().getValue() > 5) { // sab/dom
            inicio = inicio.plusDays(1);
        }
        return new LocalDateTime[]{inicio, inicio.plusHours(1)};
    }

    private InstrucaoRequestDTO request(Long alunoId, Long instrutorId, LocalDateTime[] janela) {
        return InstrucaoRequestDTO.builder()
                .alunoId(alunoId)
                .instrutorId(instrutorId)
                .dataHoraInicio(janela[0])
                .dataHoraFim(janela[1])
                .categoria("B")
                .observacao("Aula pratica")
                .build();
    }

    @BeforeEach
    void setUp() {
        org.mockito.BDDMockito.given(viaCepClient.consultarCep(org.mockito.ArgumentMatchers.any()))
                .willReturn(com.fiap.autoescola.client.ViaCepResponse.builder()
                        .cep("01310100").logradouro("Av. Paulista").bairro("Bela Vista")
                        .localidade("Sao Paulo").uf("SP").build());
    }

    private Long criarAluno(String cpf) {
        return alunoService.criar(AlunoRequestDTO.builder()
                .nome("Aluno " + cpf).cpf(cpf)
                .dataNascimento(LocalDate.of(1998, 3, 3))
                .endereco(EnderecoDTO.builder().cep("01310-100").build())
                .build()).getId();
    }

    private Long criarInstrutor(String cpf, String cnh) {
        return instrutorService.criar(InstrutorRequestDTO.builder()
                .nome("Instrutor " + cnh).cpf(cpf).cnh(cnh)
                .dataNascimento(LocalDate.of(1980, 7, 7))
                .endereco(EnderecoDTO.builder().cep("01310-100").build())
                .build()).getId();
    }

    @Test
    void deveAgendarInstrucaoComCreated() throws Exception {
        Long alunoId = criarAluno("333.111.000-01");
        Long instrutorId = criarInstrutor("444.111.000-01", "CNH-A1");

        mockMvc.perform(post("/api/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(alunoId, instrutorId, proximaJanelaUtil(10)))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("AGENDADA"))
                .andExpect(jsonPath("$.alunoId").value(alunoId))
                .andExpect(jsonPath("$.instrutorId").value(instrutorId));
    }

    @Test
    void deveRejeitarAgendamentoSemAntecedenciaMinima_R5() throws Exception {
        Long alunoId = criarAluno("333.111.000-02");
        Long instrutorId = criarInstrutor("444.111.000-02", "CNH-A2");

        LocalDateTime amanha = LocalDateTime.now().plusHours(30)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
        // mesmo valido em dias, antecedencia < 24h apos recalcular? usar agora+2h para garantir violacao
        LocalDateTime inicio = LocalDateTime.now().plusHours(2)
                .withSecond(0).withNano(0);
        InstrucaoRequestDTO dto = request(alunoId, instrutorId,
                new LocalDateTime[]{inicio.withHour(Math.min(inicio.getHour(), 17)), amanha});
        dto.setDataHoraInicio(inicio);
        dto.setDataHoraFim(inicio.plusHours(1));

        mockMvc.perform(post("/api/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarDuracaoMaiorQueQuatroHoras_R1() throws Exception {
        Long alunoId = criarAluno("333.111.000-03");
        Long instrutorId = criarInstrutor("444.111.000-03", "CNH-A3");

        LocalDateTime[] janela = proximaJanelaUtil(9);
        InstrucaoRequestDTO dto = request(alunoId, instrutorId, janela);
        dto.setDataHoraFim(janela[0].plusHours(5));

        mockMvc.perform(post("/api/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRejeitarConflitoDeAgendaDoInstrutor_R3() throws Exception {
        Long alunoId = criarAluno("333.111.000-04");
        Long instrutorId = criarInstrutor("444.111.000-04", "CNH-A4");

        LocalDateTime[] janela = proximaJanelaUtil(10);
        mockMvc.perform(post("/api/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(alunoId, instrutorId, janela))))
                .andExpect(status().isCreated());

        // segundo aluno, MESMO instrutor e MESMO horario -> conflito
        Long outroAluno = criarAluno("333.111.000-44");
        mockMvc.perform(post("/api/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(outroAluno, instrutorId, janela))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveCancelarInstrucaoAgendada_R6() throws Exception {
        Long alunoId = criarAluno("333.111.000-05");
        Long instrutorId = criarInstrutor("444.111.000-05", "CNH-A5");

        String corpo = mockMvc.perform(post("/api/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(alunoId, instrutorId, proximaJanelaUtil(14)))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpo).get("id").asLong();

        CancelamentoDTO motivo = CancelamentoDTO.builder().motivo("Imprevisto do aluno").build();
        mockMvc.perform(patch("/api/instrucoes/{id}/cancelamento", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motivo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        // cancelar novamente nao e permitido
        mockMvc.perform(patch("/api/instrucoes/{id}/cancelamento", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motivo)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveListarPorAlunoEBuscarPorId() throws Exception {
        Long alunoId = criarAluno("333.111.000-06");
        Long instrutorId = criarInstrutor("444.111.000-06", "CNH-A6");

        String corpo = mockMvc.perform(post("/api/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(alunoId, instrutorId, proximaJanelaUtil(16)))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(corpo).get("id").asLong();

        mockMvc.perform(get("/api/instrucoes/aluno/{alunoId}", alunoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());

        mockMvc.perform(get("/api/instrucoes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGENDADA"));

        mockMvc.perform(delete("/api/instrucoes/{id}", id))
                .andExpect(status().isNoContent());
    }
}
