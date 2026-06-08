package com.votacao.exception;

public class VotoDuplicadoException extends RuntimeException {
    public VotoDuplicadoException(String associadoId) {
        super("O associado já votou nesta sessão: " + associadoId);
    }
}
