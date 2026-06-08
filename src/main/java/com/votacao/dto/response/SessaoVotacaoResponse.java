package com.votacao.dto.response;

import com.votacao.entity.SessaoVotacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados da sessão de votação")
public record SessaoVotacaoResponse(

    @Schema(description = "Identificador único da sessão")
    UUID id,

    @Schema(description = "Identificador da pauta")
    UUID pautaId,

    @Schema(description = "Título da pauta")
    String pautaTitulo,

    @Schema(description = "Data e hora de abertura da sessão")
    LocalDateTime abertura,

    @Schema(description = "Data e hora de encerramento da sessão")
    LocalDateTime encerramento,

    @Schema(description = "Indica se a sessão está ativa")
    boolean ativa
) {
    public static SessaoVotacaoResponse from(SessaoVotacao sessao) {
        return new SessaoVotacaoResponse(
            sessao.getId(),
            sessao.getPauta().getId(),
            sessao.getPauta().getTitulo(),
            sessao.getAbertura(),
            sessao.getEncerramento(),
            sessao.isAtiva()
        );
    }
}
