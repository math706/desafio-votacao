package com.votacao.dto.request;

import com.votacao.enums.OpcaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para registro de voto")
public record RegistrarVotoRequest(

    @NotBlank(message = "O ID do associado é obrigatório")
    @Schema(description = "CPF ou identificador único do associado", example = "12345678901")
    String associadoId,

    @NotNull(message = "O voto é obrigatório")
    @Schema(description = "Opção de voto: SIM ou NAO", example = "SIM")
    OpcaoVoto voto
) {}
