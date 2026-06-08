package com.votacao.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Dados para abertura de sessão de votação")
public record AbrirSessaoRequest(

    @Min(value = 1, message = "A duração mínima é de 1 minuto")
    @Schema(description = "Duração da sessão em minutos. Padrão: 1 minuto", example = "5")
    Integer duracaoMinutos
) {}
