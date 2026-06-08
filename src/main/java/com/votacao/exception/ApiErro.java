package com.votacao.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErro(
    int status,
    String erro,
    String mensagem,
    LocalDateTime timestamp,
    List<String> detalhes
) {
    public static ApiErro of(int status, String erro, String mensagem) {
        return ApiErro.builder()
            .status(status)
            .erro(erro)
            .mensagem(mensagem)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static ApiErro of(int status, String erro, String mensagem, List<String> detalhes) {
        return ApiErro.builder()
            .status(status)
            .erro(erro)
            .mensagem(mensagem)
            .timestamp(LocalDateTime.now())
            .detalhes(detalhes)
            .build();
    }
}
