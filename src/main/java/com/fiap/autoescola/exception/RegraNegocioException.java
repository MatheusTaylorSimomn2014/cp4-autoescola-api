package com.fiap.autoescola.exception;

/**
 * Excecao para violacoes de regras de negocio (agendamento, cancelamento etc.)
 */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
