package com.fiap.autoescola.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "instrutor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 18)
    private String cpf;

    @Column(nullable = false, unique = true, length = 20)
    private String cnh;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;
}
