package com.votacao.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de uma nova pauta")
public record CriarPautaRequest(

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 3, max = 255, message = "O título deve ter entre 3 e 255 caracteres")
    @Schema(description = "Título da pauta", example = "Aprovação do orçamento 2025")
    String titulo,

    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
    @Schema(description = "Descrição detalhada da pauta", example = "Votação para aprovação do orçamento anual de 2025")
    String descricao
) {}
