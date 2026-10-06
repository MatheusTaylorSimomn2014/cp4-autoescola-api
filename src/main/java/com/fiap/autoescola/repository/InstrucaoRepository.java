package com.fiap.autoescola.repository;

import com.fiap.autoescola.model.Instrucao;
import com.fiap.autoescola.model.StatusInstrucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {

    List<Instrucao> findByAlunoId(Long alunoId);

    List<Instrucao> findByInstrutorId(Long instrutorId);

    /**
     * Busca instrucoes ativas (nao canceladas) de um instrutor que se sobrepoe
     * ao intervalo informado - usada na regra de conflito de agenda.
     */
    @Query("""
            SELECT i FROM Instrucao i
            WHERE i.instrutor.id = :instrutorId
              AND i.status <> com.fiap.autoescola.model.StatusInstrucao.CANCELADA
              AND i.dataHoraInicio < :fim
              AND i.dataHoraFim > :inicio
            """)
    List<Instrucao> findConflitosInstrutor(@Param("instrutorId") Long instrutorId,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fim") LocalDateTime fim);

    @Query("""
            SELECT COUNT(i) FROM Instrucao i
            WHERE i.aluno.id = :alunoId
              AND i.status IN (:statusList)
            """)
    long countByAlunoAndStatusIn(@Param("alunoId") Long alunoId,
                                 @Param("statusList") List<StatusInstrucao> statusList);
}
