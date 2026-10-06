package br.com.fiap.autoescola.dto;

import br.com.fiap.autoescola.entity.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrucaoCancelamentoDTO {
    @NotNull
    private MotivoCancelamento motivo;
}