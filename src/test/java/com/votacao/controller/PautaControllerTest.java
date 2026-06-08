package com.votacao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votacao.dto.request.CriarPautaRequest;
import com.votacao.dto.response.PautaResponse;
import com.votacao.dto.response.ResultadoVotacaoResponse;
import com.votacao.exception.GlobalExceptionHandler;
import com.votacao.exception.PautaNaoEncontradaException;
import com.votacao.exception.SessaoNaoEncontradaException;
import com.votacao.service.PautaService;
import com.votacao.service.SessaoVotacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PautaController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("PautaController")
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PautaService pautaService;

    @MockBean
    private SessaoVotacaoService sessaoVotacaoService;

    @Test
    @DisplayName("POST /api/v1/pautas - deve criar pauta e retornar 201")
    void deveCriarPauta() throws Exception {
        UUID id = UUID.randomUUID();
        CriarPautaRequest request = new CriarPautaRequest("Aprovação orçamento", "Descrição");
        PautaResponse response = new PautaResponse(id, "Aprovação orçamento", "Descrição", LocalDateTime.now());

        when(pautaService.criar(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.titulo").value("Aprovação orçamento"));
    }

    @Test
    @DisplayName("POST /api/v1/pautas - deve retornar 400 quando título vazio")
    void deveRetornar400QuandoTituloVazio() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest("", null);

        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /api/v1/pautas - deve listar pautas")
    void deveListarPautas() throws Exception {
        UUID id = UUID.randomUUID();
        when(pautaService.listar()).thenReturn(
            List.of(new PautaResponse(id, "Pauta 1", null, LocalDateTime.now()))
        );

        mockMvc.perform(get("/api/v1/pautas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(id.toString()));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id} - deve retornar 404 quando pauta não existe")
    void deveRetornar404QuandoPautaNaoExiste() throws Exception {
        UUID id = UUID.randomUUID();
        when(pautaService.buscarPorId(id)).thenThrow(new PautaNaoEncontradaException(id));

        mockMvc.perform(get("/api/v1/pautas/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/resultado - deve retornar resultado")
    void deveRetornarResultado() throws Exception {
        UUID pautaId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();
        ResultadoVotacaoResponse resultado = new ResultadoVotacaoResponse(
            pautaId, "Pauta Teste", sessaoId,
            LocalDateTime.now().plusMinutes(1), true,
            10L, 7L, 3L, "APROVADA"
        );

        when(sessaoVotacaoService.obterResultado(pautaId)).thenReturn(resultado);

        mockMvc.perform(get("/api/v1/pautas/{id}/resultado", pautaId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultado").value("APROVADA"))
            .andExpect(jsonPath("$.votosSim").value(7))
            .andExpect(jsonPath("$.votosNao").value(3));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/resultado - deve retornar 404 quando sessão não existe")
    void deveRetornar404QuandoSessaoNaoExiste() throws Exception {
        UUID pautaId = UUID.randomUUID();
        when(sessaoVotacaoService.obterResultado(pautaId))
            .thenThrow(new SessaoNaoEncontradaException("Pauta não possui sessão: " + pautaId));

        mockMvc.perform(get("/api/v1/pautas/{id}/resultado", pautaId))
            .andExpect(status().isNotFound());
    }
}
