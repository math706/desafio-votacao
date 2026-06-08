package com.votacao.exception;

import java.util.UUID;

public class SessaoNaoEncontradaException extends RuntimeException {
    public SessaoNaoEncontradaException(UUID id) {
        super("Sessão de votação não encontrada: " + id);
    }

    public SessaoNaoEncontradaException(String message) {
        super(message);
    }
}
