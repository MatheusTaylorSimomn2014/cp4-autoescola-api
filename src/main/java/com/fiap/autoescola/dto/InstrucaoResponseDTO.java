package com.fiap.autoescola.dto;

import com.fiap.autoescola.model.Instrucao;
import com.fiap.autoescola.model.StatusInstrucao;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstrucaoResponseDTO {
    private Long id;
    private Long alunoId;
    private String alunoNome;
    private Long instrutorId;
    private String instrutorNome;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String categoria;
    private StatusInstrucao status;
    private String observacao;
    private LocalDateTime criadoEm;

    public static InstrucaoResponseDTO fromEntity(Instrucao i) {
        return InstrucaoResponseDTO.builder()
                .id(i.getId())
                .alunoId(i.getAluno().getId())
                .alunoNome(i.getAluno().getNome())
                .instrutorId(i.getInstrutor().getId())
                .instrutorNome(i.getInstrutor().getNome())
                .dataHoraInicio(i.getDataHoraInicio())
                .dataHoraFim(i.getDataHoraFim())
                .categoria(i.getCategoria())
                .status(i.getStatus())
                .observacao(i.getObservacao())
                .criadoEm(i.getCriadoEm())
                .build();
    }
}
