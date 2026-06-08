package com.votacao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votacao.dto.request.RegistrarVotoRequest;
import com.votacao.dto.response.VotoResponse;
import com.votacao.enums.OpcaoVoto;
import com.votacao.exception.GlobalExceptionHandler;
import com.votacao.exception.SessaoEncerradaException;
import com.votacao.exception.VotoDuplicadoException;
import com.votacao.service.VotoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotoController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("VotoController")
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VotoService votoService;

    @Test
    @DisplayName("POST /api/v1/sessoes/{id}/votos - deve registrar voto e retornar 201")
    void deveRegistrarVoto() throws Exception {
        UUID sessaoId = UUID.randomUUID();
        UUID votoId = UUID.randomUUID();
        RegistrarVotoRequest request = new RegistrarVotoRequest("12345678901", OpcaoVoto.SIM);
        VotoResponse response = new VotoResponse(votoId, sessaoId, "12345678901", OpcaoVoto.SIM, LocalDateTime.now());

        when(votoService.registrar(eq(sessaoId), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/sessoes/{sessaoId}/votos", sessaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.voto").value("SIM"))
            .andExpect(jsonPath("$.associadoId").value("12345678901"));
    }

    @Test
    @DisplayName("POST - deve retornar 422 quando sessão encerrada")
    void deveRetornar422QuandoSessaoEncerrada() throws Exception {
        UUID sessaoId = UUID.randomUUID();
        RegistrarVotoRequest request = new RegistrarVotoRequest("12345678901", OpcaoVoto.SIM);

        when(votoService.registrar(eq(sessaoId), any()))
            .thenThrow(new SessaoEncerradaException(sessaoId));

        mockMvc.perform(post("/api/v1/sessoes/{sessaoId}/votos", sessaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST - deve retornar 409 quando voto duplicado")
    void deveRetornar409QuandoVotoDuplicado() throws Exception {
        UUID sessaoId = UUID.randomUUID();
        RegistrarVotoRequest request = new RegistrarVotoRequest("12345678901", OpcaoVoto.SIM);

        when(votoService.registrar(eq(sessaoId), any()))
            .thenThrow(new VotoDuplicadoException("12345678901"));

        mockMvc.perform(post("/api/v1/sessoes/{sessaoId}/votos", sessaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST - deve retornar 400 quando voto nulo")
    void deveRetornar400QuandoVotoNulo() throws Exception {
        UUID sessaoId = UUID.randomUUID();
        String requestJson = "{\"associadoId\": \"12345678901\", \"voto\": null}";

        mockMvc.perform(post("/api/v1/sessoes/{sessaoId}/votos", sessaoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }
}
