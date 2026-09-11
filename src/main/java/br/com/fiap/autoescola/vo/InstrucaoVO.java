package br.com.fiap.autoescola.vo;

import br.com.fiap.autoescola.entity.MotivoCancelamento;
import br.com.fiap.autoescola.entity.StatusInstrucao;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrucaoVO {
    private Long id;
    private Long alunoId;
    private String alunoNome;
    private Long instrutorId;
    private String instrutorNome;
    private LocalDateTime dataHora;
    private StatusInstrucao status;
    private MotivoCancelamento motivoCancelamento;
    private LocalDateTime dataCancelamento;
}