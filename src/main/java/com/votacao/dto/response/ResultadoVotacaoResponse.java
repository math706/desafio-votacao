package com.votacao.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Resultado da votação de uma pauta")
public record ResultadoVotacaoResponse(

    @Schema(description = "Identificador da pauta")
    UUID pautaId,

    @Schema(description = "Título da pauta")
    String pautaTitulo,

    @Schema(description = "Identificador da sessão de votação")
    UUID sessaoId,

    @Schema(description = "Data e hora de encerramento da sessão")
    LocalDateTime encerramento,

    @Schema(description = "Indica se a sessão ainda está ativa")
    boolean sessaoAtiva,

    @Schema(description = "Total de votos registrados")
    long totalVotos,

    @Schema(description = "Total de votos SIM")
    long votosSim,

    @Schema(description = "Total de votos NÃO")
    long votosNao,

    @Schema(description = "Resultado: APROVADA, REPROVADA ou EMPATE")
    String resultado
) {}
