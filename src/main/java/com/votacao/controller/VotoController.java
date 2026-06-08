package com.votacao.controller;

import com.votacao.dto.request.RegistrarVotoRequest;
import com.votacao.dto.response.VotoResponse;
import com.votacao.service.VotoService;
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
@RequestMapping("/api/v1/sessoes/{sessaoId}/votos")
@RequiredArgsConstructor
@Tag(name = "Votos", description = "Registro de votos em sessões de votação")
public class VotoController {

    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Registrar voto")
    @ApiResponses({
        @ApiResponse(responseCode = "409", description = "Associado já votou nesta sessão"),
        @ApiResponse(responseCode = "422", description = "Sessão encerrada")
    })
    public ResponseEntity<VotoResponse> registrar(
        @PathVariable UUID sessaoId,
        @Valid @RequestBody RegistrarVotoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.registrar(sessaoId, request));
    }
}
