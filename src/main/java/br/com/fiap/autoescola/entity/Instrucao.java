package br.com.fiap.autoescola.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "instrucoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrucao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrutor_id", nullable = false)
    private Instrutor instrutor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusInstrucao status;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cancelamento", length = 30)
    private MotivoCancelamento motivoCancelamento;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;
}