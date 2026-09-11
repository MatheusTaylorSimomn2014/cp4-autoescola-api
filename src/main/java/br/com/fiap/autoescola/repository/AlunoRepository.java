package br.com.fiap.autoescola.repository;

import br.com.fiap.autoescola.entity.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByIdAndAtivoTrue(Long id);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Page<Aluno> findAllByAtivoTrue(Pageable pageable);
}