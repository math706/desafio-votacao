package com.votacao.controller;

import com.votacao.dto.request.CriarPautaRequest;
import com.votacao.dto.response.PautaResponse;
import com.votacao.dto.response.ResultadoVotacaoResponse;
import com.votacao.service.PautaService;
import com.votacao.service.SessaoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "Gerenciamento de pautas para votação")
public class PautaController {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoService;

    @PostMapping
    @Operation(summary = "Cadastrar nova pauta")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody CriarPautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar todas as pautas")
    public ResponseEntity<List<PautaResponse>> listar() {
        return ResponseEntity.ok(pautaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.buscarPorId(id));
    }

    @GetMapping("/{id}/resultado")
    @Operation(summary = "Resultado da votação")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada ou sem sessão de votação")
    public ResponseEntity<ResultadoVotacaoResponse> resultado(@PathVariable UUID id) {
        return ResponseEntity.ok(sessaoService.obterResultado(id));
    }
}
