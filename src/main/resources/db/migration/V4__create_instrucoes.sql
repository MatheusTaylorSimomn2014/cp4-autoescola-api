CREATE TABLE instrucoes (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    instrutor_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    motivo_cancelamento VARCHAR(30),
    data_cancelamento TIMESTAMP,
    CONSTRAINT fk_instrucao_aluno FOREIGN KEY (aluno_id) REFERENCES alunos (id),
    CONSTRAINT fk_instrucao_instrutor FOREIGN KEY (instrutor_id) REFERENCES instrutores (id)
);

CREATE INDEX idx_instrucoes_instrutor_data ON instrucoes (instrutor_id, data_hora);
CREATE INDEX idx_instrucoes_aluno_data ON instrucoes (aluno_id, data_hora);