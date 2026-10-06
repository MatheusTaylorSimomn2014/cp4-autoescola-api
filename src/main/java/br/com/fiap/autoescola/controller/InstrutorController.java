package br.com.fiap.autoescola.controller;

import br.com.fiap.autoescola.dto.InstrutorCreateDTO;
import br.com.fiap.autoescola.dto.InstrutorUpdateDTO;
import br.com.fiap.autoescola.service.InstrutorService;
import br.com.fiap.autoescola.vo.InstrutorListVO;
import br.com.fiap.autoescola.vo.InstrutorVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instrutores")
@RequiredArgsConstructor
public class InstrutorController {

    private final InstrutorService instrutorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstrutorVO criar(@Valid @RequestBody InstrutorCreateDTO dto) {
        return instrutorService.criar(dto);
    }

    @GetMapping
    public Page<InstrutorListVO> listar(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("nome").ascending());
        return instrutorService.listar(pageable);
    }

    @GetMapping("/{id}")
    public InstrutorVO buscar(@PathVariable Long id) {
        return instrutorService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public InstrutorVO atualizar(@PathVariable Long id, @Valid @RequestBody InstrutorUpdateDTO dto) {
        return instrutorService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        instrutorService.excluir(id);
    }
}