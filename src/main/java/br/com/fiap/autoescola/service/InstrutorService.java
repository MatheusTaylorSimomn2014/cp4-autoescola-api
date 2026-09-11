package br.com.fiap.autoescola.service;

import br.com.fiap.autoescola.dto.InstrutorCreateDTO;
import br.com.fiap.autoescola.dto.InstrutorUpdateDTO;
import br.com.fiap.autoescola.entity.Instrutor;
import br.com.fiap.autoescola.exception.BusinessException;
import br.com.fiap.autoescola.exception.ResourceNotFoundException;
import br.com.fiap.autoescola.mapper.InstrutorMapper;
import br.com.fiap.autoescola.repository.InstrutorRepository;
import br.com.fiap.autoescola.vo.InstrutorListVO;
import br.com.fiap.autoescola.vo.InstrutorVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstrutorService {

    private final InstrutorRepository instrutorRepository;

    @Transactional
    public InstrutorVO criar(InstrutorCreateDTO dto) {
        if (instrutorRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail já cadastrado para outro instrutor.");
        }
        if (instrutorRepository.existsByCnh(dto.getCnh())) {
            throw new BusinessException("CNH já cadastrada para outro instrutor.");
        }
        Instrutor instrutor = InstrutorMapper.toEntity(dto);
        instrutor = instrutorRepository.save(instrutor);
        return InstrutorMapper.toVO(instrutor);
    }

    @Transactional(readOnly = true)
    public Page<InstrutorListVO> listar(Pageable pageable) {
        return instrutorRepository.findAllByAtivoTrue(pageable)
                .map(InstrutorMapper::toListVO);
    }

    @Transactional(readOnly = true)
    public InstrutorVO buscarPorId(Long id) {
        Instrutor instrutor = instrutorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrutor não encontrado ou inativo."));
        return InstrutorMapper.toVO(instrutor);
    }

    @Transactional
    public InstrutorVO atualizar(Long id, InstrutorUpdateDTO dto) {
        Instrutor instrutor = instrutorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrutor não encontrado ou inativo."));
        InstrutorMapper.updateEntity(instrutor, dto);
        instrutor = instrutorRepository.save(instrutor);
        return InstrutorMapper.toVO(instrutor);
    }

    @Transactional
    public void excluir(Long id) {
        Instrutor instrutor = instrutorRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instrutor não encontrado ou inativo."));
        instrutor.setAtivo(false);
        instrutorRepository.save(instrutor);
    }
}