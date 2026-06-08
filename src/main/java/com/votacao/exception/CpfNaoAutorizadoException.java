package com.votacao.exception;

public class CpfNaoAutorizadoException extends RuntimeException {
    public CpfNaoAutorizadoException(String cpf) {
        super("CPF não autorizado a votar: " + cpf);
    }
}
