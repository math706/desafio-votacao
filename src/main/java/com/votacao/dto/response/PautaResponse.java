package com.votacao.dto.response;

import com.votacao.entity.Pauta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados da pauta")
public record PautaResponse(

    @Schema(description = "Identificador único da pauta")
    UUID id,

    @Schema(description = "Título da pauta")
    String titulo,

    @Schema(description = "Descrição da pauta")
    String descricao,

    @Schema(description = "Data e hora de criação")
    LocalDateTime criadaEm
) {
    public static PautaResponse from(Pauta pauta) {
        return new PautaResponse(
            pauta.getId(),
            pauta.getTitulo(),
            pauta.getDescricao(),
            pauta.getCriadaEm()
        );
    }
}
