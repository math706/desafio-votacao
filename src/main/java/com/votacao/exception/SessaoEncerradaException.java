package com.votacao.exception;

import java.util.UUID;

public class SessaoEncerradaException extends RuntimeException {
    public SessaoEncerradaException(UUID sessaoId) {
        super("A sessão de votação já foi encerrada: " + sessaoId);
    }
}
