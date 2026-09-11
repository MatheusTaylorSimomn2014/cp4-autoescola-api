package br.com.fiap.autoescola.controller;

import br.com.fiap.autoescola.dto.InstrucaoCancelamentoDTO;
import br.com.fiap.autoescola.dto.InstrucaoCreateDTO;
import br.com.fiap.autoescola.service.InstrucaoService;
import br.com.fiap.autoescola.vo.InstrucaoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instrucoes")
@RequiredArgsConstructor
public class InstrucaoController {

    private final InstrucaoService instrucaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstrucaoVO agendar(@Valid @RequestBody InstrucaoCreateDTO dto) {
        return instrucaoService.agendar(dto);
    }

    @GetMapping
    public List<InstrucaoVO> listar() {
        return instrucaoService.listar();
    }

    @GetMapping("/{id}")
    public InstrucaoVO buscar(@PathVariable Long id) {
        return instrucaoService.buscarPorId(id);
    }

    @PutMapping("/{id}/cancelamento")
    public InstrucaoVO cancelar(@PathVariable Long id,
                                @Valid @RequestBody InstrucaoCancelamentoDTO dto) {
        return instrucaoService.cancelar(id, dto);
    }
}