package com.fiap.autoescola.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Motivo de cancelamento da instrucao")
public class CancelamentoDTO {

    @NotBlank(message = "Motivo do cancelamento e obrigatorio")
    @Schema(example = "Aluno justificou falta")
    private String motivo;
}
