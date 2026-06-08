package com.votacao.controller;

import com.votacao.dto.request.AbrirSessaoRequest;
import com.votacao.dto.response.SessaoVotacaoResponse;
import com.votacao.service.SessaoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessoes")
@RequiredArgsConstructor
@Tag(name = "Sessões de Votação", description = "Abertura e consulta de sessões de votação")
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoService;

    @PostMapping
    @Operation(
        summary = "Abrir sessão de votação",
        description = "Duração padrão: 1 minuto. Cada pauta comporta apenas uma sessão."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
        @ApiResponse(responseCode = "409", description = "Pauta já possui uma sessão de votação")
    })
    public ResponseEntity<SessaoVotacaoResponse> abrir(
        @PathVariable UUID pautaId,
        @Valid @RequestBody(required = false) AbrirSessaoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoService.abrir(pautaId, request));
    }

    @GetMapping("/{sessaoId}")
    @Operation(summary = "Consultar sessão de votação")
    public ResponseEntity<SessaoVotacaoResponse> buscarPorId(
        @PathVariable UUID pautaId,
        @PathVariable UUID sessaoId
    ) {
        return ResponseEntity.ok(sessaoService.buscarPorId(sessaoId));
    }
}
