package br.com.fiap.autoescola.repository;

import br.com.fiap.autoescola.entity.Instrutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
    Optional<Instrutor> findByIdAndAtivoTrue(Long id);
    boolean existsByEmail(String email);
    boolean existsByCnh(String cnh);
    Page<Instrutor> findAllByAtivoTrue(Pageable pageable);
}