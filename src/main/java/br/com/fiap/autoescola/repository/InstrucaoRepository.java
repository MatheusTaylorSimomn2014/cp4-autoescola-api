package br.com.fiap.autoescola.repository;

import br.com.fiap.autoescola.entity.Instrucao;
import br.com.fiap.autoescola.entity.StatusInstrucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {

    @Query("SELECT i FROM Instrucao i WHERE i.instrutor.id = :instrutorId " +
           "AND i.dataHora = :dataHora AND i.status = 'AGENDADA'")
    List<Instrucao> findByInstrutorAndDataHora(
            @Param("instrutorId") Long instrutorId,
            @Param("dataHora") LocalDateTime dataHora);

    @Query("SELECT COUNT(i) FROM Instrucao i WHERE i.aluno.id = :alunoId " +
           "AND i.dataHora BETWEEN :inicio AND :fim AND i.status = 'AGENDADA'")
    long countByAlunoAndDia(
            @Param("alunoId") Long alunoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("SELECT i.instrutor.id FROM Instrucao i WHERE i.dataHora = :dataHora " +
           "AND i.status = 'AGENDADA'")
    List<Long> findInstrutoresOcupados(@Param("dataHora") LocalDateTime dataHora);

    @Query("SELECT i FROM Instrucao i WHERE i.aluno.id = :alunoId " +
           "AND i.status = :status ORDER BY i.dataHora DESC")

    @Query("SELECT i FROM Instrutor i WHERE i.ativo = true AND i.id NOT IN :ocupados")
    List<Instrutor> findDisponiveis(@Param("ocupados") List<Long> ocupados);

    List<Instrucao> findByAlunoAndStatus(
            @Param("alunoId") Long alunoId,
            @Param("status") StatusInstrucao status);
}