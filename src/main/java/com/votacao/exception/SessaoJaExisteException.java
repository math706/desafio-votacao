package com.votacao.exception;

import java.util.UUID;

public class SessaoJaExisteException extends RuntimeException {
    public SessaoJaExisteException(UUID pautaId) {
        super("Esta pauta já possui uma sessão de votação: " + pautaId);
    }
}
