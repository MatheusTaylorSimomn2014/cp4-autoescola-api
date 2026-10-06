package br.com.fiap.autoescola.controller;

import br.com.fiap.autoescola.dto.AlunoCreateDTO;
import br.com.fiap.autoescola.dto.AlunoUpdateDTO;
import br.com.fiap.autoescola.service.AlunoService;
import br.com.fiap.autoescola.vo.AlunoListVO;
import br.com.fiap.autoescola.vo.AlunoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlunoVO criar(@Valid @RequestBody AlunoCreateDTO dto) {
        return alunoService.criar(dto);
    }

    @GetMapping
    public Page<AlunoListVO> listar(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("nome").ascending());
        return alunoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public AlunoVO buscar(@PathVariable Long id) {
        return alunoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AlunoVO atualizar(@PathVariable Long id, @Valid @RequestBody AlunoUpdateDTO dto) {
        return alunoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        alunoService.excluir(id);
    }
}
