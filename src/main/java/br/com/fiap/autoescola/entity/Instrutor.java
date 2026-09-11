package br.com.fiap.autoescola.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instrutores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instrutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, unique = true, length = 20)
    private String cnh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Especialidade especialidade;

    @Embedded
    private Endereco endereco;

    @Column(nullable = false)
    private Boolean ativo = true;
}