package com.votacao.controller;

import com.votacao.service.CpfValidacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Validação de CPF", description = "Verificação de elegibilidade para votação")
public class CpfController {

    private final CpfValidacaoService cpfValidacaoService;

    @GetMapping("/{cpf}")
    @Operation(summary = "Verificar elegibilidade do CPF para votar")
    @ApiResponse(responseCode = "404", description = "CPF inválido")
    public ResponseEntity<Map<String, String>> verificar(@PathVariable String cpf) {
        String status = cpfValidacaoService.consultarStatus(cpf);
        return ResponseEntity.ok(Map.of("status", status));
    }
}
