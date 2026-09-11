package br.com.fiap.autoescola.service;

import br.com.fiap.autoescola.dto.AlunoCreateDTO;
import br.com.fiap.autoescola.dto.AlunoUpdateDTO;
import br.com.fiap.autoescola.entity.Aluno;
import br.com.fiap.autoescola.exception.BusinessException;
import br.com.fiap.autoescola.exception.ResourceNotFoundException;
import br.com.fiap.autoescola.mapper.AlunoMapper;
import br.com.fiap.autoescola.repository.AlunoRepository;
import br.com.fiap.autoescola.vo.AlunoListVO;
import br.com.fiap.autoescola.vo.AlunoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    @Transactional
    public AlunoVO criar(AlunoCreateDTO dto) {
        if (alunoRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail já cadastrado para outro aluno.");
        }
        if (alunoRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado para outro aluno.");
        }
        Aluno aluno = AlunoMapper.toEntity(dto);
        aluno = alunoRepository.save(aluno);
        return AlunoMapper.toVO(aluno);
    }

    @Transactional(readOnly = true)
    public Page<AlunoListVO> listar(Pageable pageable) {
        return alunoRepository.findAll(pageable)
                .map(AlunoMapper::toListVO);
    }

    @Transactional(readOnly = true)
    public AlunoVO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado."));
        return AlunoMapper.toVO(aluno);
    }

    @Transactional
    public AlunoVO atualizar(Long id, AlunoUpdateDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado."));
        AlunoMapper.updateEntity(aluno, dto);
        aluno = alunoRepository.save(aluno);
        return AlunoMapper.toVO(aluno);
    }

    @Transactional
    public void excluir(Long id) {
        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado ou inativo."));
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }
}