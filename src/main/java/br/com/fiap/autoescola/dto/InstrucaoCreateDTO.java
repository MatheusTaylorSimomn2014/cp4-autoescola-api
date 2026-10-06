package br.com.fiap.autoescola.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrucaoCreateDTO {
    @NotNull
    private Long alunoId;

    // Opcional – se não informado, o sistema escolhe aleatoriamente
    private Long instrutorId;

    @NotNull
    private LocalDateTime dataHora;
}