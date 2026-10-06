package com.fiap.autoescola.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "instrucao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "instrutor_id")
    private Instrutor instrutor;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(nullable = false)
    private LocalDateTime dataHoraFim;

    /** Categoria da aula pratica: A (moto), B (carro), etc. */
    @Column(nullable = false, length = 5)
    @Builder.Default
    private String categoria = "B";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusInstrucao status = StatusInstrucao.AGENDADA;

    @Column(length = 500)
    private String observacao;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();
}
