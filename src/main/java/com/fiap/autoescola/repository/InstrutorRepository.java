package com.fiap.autoescola.repository;

import com.fiap.autoescola.model.Instrutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
    Optional<Instrutor> findByCpf(String cpf);
    Optional<Instrutor> findByCnh(String cnh);
    boolean existsByCpf(String cpf);
    boolean existsByCnh(String cnh);
}
