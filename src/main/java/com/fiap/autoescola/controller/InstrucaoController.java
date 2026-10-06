package com.fiap.autoescola.controller;

import com.fiap.autoescola.dto.CancelamentoDTO;
import com.fiap.autoescola.dto.InstrucaoRequestDTO;
import com.fiap.autoescola.dto.InstrucaoResponseDTO;
import com.fiap.autoescola.service.InstrucaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/instrucoes")
@Tag(name = "Instrucoes", description = "Agendamento e cancelamento de aulas praticas (instrucoes)")
public class InstrucaoController {

    private final InstrucaoService service;

    public InstrucaoController(InstrucaoService service) {
        this.service = service;
    }

    @Operation(summary = "Agenda uma nova instrucao respeitando as regras de negocio")
    @PostMapping
    public ResponseEntity<InstrucaoResponseDTO> agendar(@Valid @RequestBody InstrucaoRequestDTO dto) {
        InstrucaoResponseDTO criada = service.agendar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @Operation(summary = "Lista todas as instrucoes")
    @GetMapping
    public List<InstrucaoResponseDTO> listar() {
        return service.listarTodas();
    }

    @Operation(summary = "Lista instrucoes de um aluno")
    @GetMapping("/aluno/{alunoId}")
    public List<InstrucaoResponseDTO> listarPorAluno(@PathVariable Long alunoId) {
        return service.listarPorAluno(alunoId);
    }

    @Operation(summary = "Lista instrucoes de um instrutor")
    @GetMapping("/instrutor/{instrutorId}")
    public List<InstrucaoResponseDTO> listarPorInstrutor(@PathVariable Long instrutorId) {
        return service.listarPorInstrutor(instrutorId);
    }

    @Operation(summary = "Busca uma instrucao pelo id")
    @GetMapping("/{id}")
    public InstrucaoResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Cancela uma instrucao agendada (antecedencia minima de 24h)")
    @PatchMapping("/{id}/cancelamento")
    public InstrucaoResponseDTO cancelar(@PathVariable Long id,
                                         @Valid @RequestBody CancelamentoDTO dto) {
        return service.cancelar(id, dto.getMotivo());
    }

    @Operation(summary = "Conclui uma instrucao (registrando a aula realizada)")
    @PatchMapping("/{id}/conclusao")
    public InstrucaoResponseDTO concluir(@PathVariable Long id) {
        return service.concluir(id);
    }

    @Operation(summary = "Remove uma instrucao pelo id (gestao administrativa)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
