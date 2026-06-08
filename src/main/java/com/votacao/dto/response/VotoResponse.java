package com.votacao.dto.response;

import com.votacao.entity.Voto;
import com.votacao.enums.OpcaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Confirmação do voto registrado")
public record VotoResponse(

    @Schema(description = "Identificador único do voto")
    UUID id,

    @Schema(description = "Identificador da sessão")
    UUID sessaoId,

    @Schema(description = "Identificador do associado")
    String associadoId,

    @Schema(description = "Opção votada: SIM ou NAO")
    OpcaoVoto voto,

    @Schema(description = "Data e hora do registro do voto")
    LocalDateTime registradoEm
) {
    public static VotoResponse from(Voto voto) {
        return new VotoResponse(
            voto.getId(),
            voto.getSessao().getId(),
            voto.getAssociadoId(),
            voto.getVoto(),
            voto.getRegistradoEm()
        );
    }
}
