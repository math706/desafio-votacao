package com.votacao.service;

import com.votacao.dto.request.AbrirSessaoRequest;
import com.votacao.dto.response.ResultadoVotacaoResponse;
import com.votacao.dto.response.SessaoVotacaoResponse;
import com.votacao.entity.Pauta;
import com.votacao.entity.SessaoVotacao;
import com.votacao.enums.OpcaoVoto;
import com.votacao.exception.SessaoJaExisteException;
import com.votacao.exception.SessaoNaoEncontradaException;
import com.votacao.repository.SessaoVotacaoRepository;
import com.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessaoVotacaoService")
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;
    @Mock
    private VotoRepository votoRepository;
    @Mock
    private PautaService pautaService;

    @InjectMocks
    private SessaoVotacaoService sessaoService;

    private Pauta pauta;
    private SessaoVotacao sessao;
    private UUID pautaId;
    private UUID sessaoId;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sessaoService, "duracaoPadraoMinutos", 1);

        pautaId = UUID.randomUUID();
        sessaoId = UUID.randomUUID();

        pauta = Pauta.builder()
            .id(pautaId)
            .titulo("Pauta Teste")
            .criadaEm(LocalDateTime.now())
            .build();

        sessao = SessaoVotacao.builder()
            .id(sessaoId)
            .pauta(pauta)
            .abertura(LocalDateTime.now())
            .encerramento(LocalDateTime.now().plusMinutes(5))
            .build();
    }

    @Test
    @DisplayName("deve abrir sessão com duração padrão")
    void deveAbrirSessaoComDuracaoPadrao() {
        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(false);
        when(pautaService.getPauta(pautaId)).thenReturn(pauta);
        when(sessaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessao);

        SessaoVotacaoResponse response = sessaoService.abrir(pautaId, null);

        assertThat(response).isNotNull();
        assertThat(response.pautaId()).isEqualTo(pautaId);
        verify(sessaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("deve abrir sessão com duração customizada")
    void deveAbrirSessaoComDuracaoCustomizada() {
        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(false);
        when(pautaService.getPauta(pautaId)).thenReturn(pauta);
        when(sessaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessao);

        SessaoVotacaoResponse response = sessaoService.abrir(pautaId, new AbrirSessaoRequest(10));

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("deve lançar exceção quando pauta já possui sessão")
    void deveLancarExcecaoQuandoPautaJaTemSessao() {
        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(true);

        assertThatThrownBy(() -> sessaoService.abrir(pautaId, null))
            .isInstanceOf(SessaoJaExisteException.class);
    }

    @Test
    @DisplayName("deve retornar resultado APROVADA quando SIM > NAO")
    void deveRetornarResultadoAprovada() {
        when(pautaService.getPauta(pautaId)).thenReturn(pauta);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(votoRepository.countBySessaoIdAndVoto(sessaoId, OpcaoVoto.SIM)).thenReturn(7L);
        when(votoRepository.countBySessaoIdAndVoto(sessaoId, OpcaoVoto.NAO)).thenReturn(3L);

        ResultadoVotacaoResponse resultado = sessaoService.obterResultado(pautaId);

        assertThat(resultado.resultado()).isEqualTo("APROVADA");
        assertThat(resultado.votosSim()).isEqualTo(7);
        assertThat(resultado.votosNao()).isEqualTo(3);
        assertThat(resultado.totalVotos()).isEqualTo(10);
    }

    @Test
    @DisplayName("deve retornar resultado REPROVADA quando NAO > SIM")
    void deveRetornarResultadoReprovada() {
        when(pautaService.getPauta(pautaId)).thenReturn(pauta);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(votoRepository.countBySessaoIdAndVoto(sessaoId, OpcaoVoto.SIM)).thenReturn(2L);
        when(votoRepository.countBySessaoIdAndVoto(sessaoId, OpcaoVoto.NAO)).thenReturn(8L);

        ResultadoVotacaoResponse resultado = sessaoService.obterResultado(pautaId);

        assertThat(resultado.resultado()).isEqualTo("REPROVADA");
    }

    @Test
    @DisplayName("deve retornar resultado EMPATE quando SIM == NAO")
    void deveRetornarResultadoEmpate() {
        when(pautaService.getPauta(pautaId)).thenReturn(pauta);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(votoRepository.countBySessaoIdAndVoto(sessaoId, OpcaoVoto.SIM)).thenReturn(5L);
        when(votoRepository.countBySessaoIdAndVoto(sessaoId, OpcaoVoto.NAO)).thenReturn(5L);

        ResultadoVotacaoResponse resultado = sessaoService.obterResultado(pautaId);

        assertThat(resultado.resultado()).isEqualTo("EMPATE");
    }

    @Test
    @DisplayName("deve lançar exceção quando pauta não tem sessão")
    void deveLancarExcecaoQuandoPautaNaoTemSessao() {
        when(pautaService.getPauta(pautaId)).thenReturn(pauta);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessaoService.obterResultado(pautaId))
            .isInstanceOf(SessaoNaoEncontradaException.class);
    }
}
