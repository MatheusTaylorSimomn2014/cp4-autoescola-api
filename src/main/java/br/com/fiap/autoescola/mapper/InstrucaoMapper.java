package br.com.fiap.autoescola.mapper;

import br.com.fiap.autoescola.entity.Instrucao;
import br.com.fiap.autoescola.vo.InstrucaoVO;

public class InstrucaoMapper {
    public static InstrucaoVO toVO(Instrucao entity) {
        return InstrucaoVO.builder()
                .id(entity.getId())
                .alunoId(entity.getAluno().getId())
                .alunoNome(entity.getAluno().getNome())
                .instrutorId(entity.getInstrutor().getId())
                .instrutorNome(entity.getInstrutor().getNome())
                .dataHora(entity.getDataHora())
                .status(entity.getStatus())
                .motivoCancelamento(entity.getMotivoCancelamento())
                .dataCancelamento(entity.getDataCancelamento())
                .build();
    }
}