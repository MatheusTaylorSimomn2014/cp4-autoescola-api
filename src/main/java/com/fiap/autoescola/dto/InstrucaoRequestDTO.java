package com.fiap.autoescola.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload para agendamento de uma instrucao (aula pratica)")
public class InstrucaoRequestDTO {

    @NotNull(message = "Id do aluno e obrigatorio")
    @Schema(example = "1")
    private Long alunoId;

    @NotNull(message = "Id do instrutor e obrigatorio")
    @Schema(example = "1")
    private Long instrutorId;

    @NotNull(message = "Data/hora de inicio e obrigatoria")
    @Schema(example = "2026-10-20T14:00:00")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "Data/hora de termino e obrigatoria")
    @Schema(example = "2026-10-20T15:00:00")
    private LocalDateTime dataHoraFim;

    @Schema(example = "B", description = "Categoria da aula: A (moto), B (carro)...")
    @Builder.Default
    private String categoria = "B";

    @Schema(example = "Aula de baliza")
    private String observacao;
}
