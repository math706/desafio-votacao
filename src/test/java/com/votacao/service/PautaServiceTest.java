package com.votacao.service;

import com.votacao.dto.request.CriarPautaRequest;
import com.votacao.dto.response.PautaResponse;
import com.votacao.entity.Pauta;
import com.votacao.exception.PautaNaoEncontradaException;
import com.votacao.repository.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PautaService")
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    private Pauta pauta;
    private UUID pautaId;

    @BeforeEach
    void setUp() {
        pautaId = UUID.randomUUID();
        pauta = Pauta.builder()
            .id(pautaId)
            .titulo("Aprovação orçamento 2025")
            .descricao("Votação para aprovação do orçamento anual")
            .criadaEm(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("deve criar pauta com sucesso")
    void deveCriarPauta() {
        CriarPautaRequest request = new CriarPautaRequest("Aprovação orçamento 2025", "Descrição");
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        PautaResponse response = pautaService.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.titulo()).isEqualTo("Aprovação orçamento 2025");
        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    @DisplayName("deve listar todas as pautas")
    void deveListarPautas() {
        when(pautaRepository.findAll()).thenReturn(List.of(pauta));

        List<PautaResponse> response = pautaService.listar();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).id()).isEqualTo(pautaId);
    }

    @Test
    @DisplayName("deve buscar pauta por ID com sucesso")
    void deveBuscarPautaPorId() {
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        PautaResponse response = pautaService.buscarPorId(pautaId);

        assertThat(response.id()).isEqualTo(pautaId);
        assertThat(response.titulo()).isEqualTo("Aprovação orçamento 2025");
    }

    @Test
    @DisplayName("deve lançar exceção quando pauta não encontrada")
    void deveLancarExcecaoQuandoPautaNaoEncontrada() {
        UUID idInexistente = UUID.randomUUID();
        when(pautaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pautaService.buscarPorId(idInexistente))
            .isInstanceOf(PautaNaoEncontradaException.class)
            .hasMessageContaining(idInexistente.toString());
    }
}
