package com.fiap.autoescola.exception;

/**
 * Falha na comunicacao com servicos externos (ex.: ViaCEP).
 */
public class ServicoExternoException extends RuntimeException {
    public ServicoExternoException(String mensagem) {
        super(mensagem);
    }

    public ServicoExternoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
