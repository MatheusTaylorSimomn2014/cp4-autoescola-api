package com.fiap.autoescola.controller;

import com.fiap.autoescola.dto.InstrutorRequestDTO;
import com.fiap.autoescola.dto.InstrutorResponseDTO;
import com.fiap.autoescola.service.InstrutorService;
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
@RequestMapping("/api/instrutores")
@Tag(name = "Instrutores", description = "CRUD de instrutores da auto-escola")
public class InstrutorController {

    private final InstrutorService service;

    public InstrutorController(InstrutorService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra um novo instrutor")
    @PostMapping
    public ResponseEntity<InstrutorResponseDTO> criar(@Valid @RequestBody InstrutorRequestDTO dto) {
        InstrutorResponseDTO criado = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @Operation(summary = "Lista todos os instrutores")
    @GetMapping
    public List<InstrutorResponseDTO> listar() {
        return service.listarTodos();
    }

    @Operation(summary = "Busca um instrutor pelo id")
    @GetMapping("/{id}")
    public InstrutorResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Atualiza um instrutor existente")
    @PutMapping("/{id}")
    public InstrutorResponseDTO atualizar(@PathVariable Long id,
                                          @Valid @RequestBody InstrutorRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @Operation(summary = "Remove um instrutor pelo id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
