package com.votacao.service;

import com.votacao.exception.CpfInvalidoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class CpfValidacaoService {

    private static final Random RANDOM = new Random();

    public static final String ABLE_TO_VOTE   = "ABLE_TO_VOTE";
    public static final String UNABLE_TO_VOTE = "UNABLE_TO_VOTE";

    public String consultarStatus(String cpf) {
        String cpfNumerico = cpf.replaceAll("[^0-9]", "");

        if (!isCpfValido(cpfNumerico)) {
            log.warn("CPF inválido: {}", cpf);
            throw new CpfInvalidoException(cpf);
        }

        String status = RANDOM.nextBoolean() ? ABLE_TO_VOTE : UNABLE_TO_VOTE;
        log.info("CPF {} -> {}", cpfNumerico, status);
        return status;
    }

    private boolean isCpfValido(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigito = (soma * 10) % 11;
            if (primeiroDigito == 10) primeiroDigito = 0;
            if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) return false;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigito = (soma * 10) % 11;
            if (segundoDigito == 10) segundoDigito = 0;
            return segundoDigito == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }
}
