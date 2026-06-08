package com.votacao.service;

import com.votacao.dto.request.RegistrarVotoRequest;
import com.votacao.dto.response.VotoResponse;
import com.votacao.entity.Pauta;
import com.votacao.entity.SessaoVotacao;
import com.votacao.entity.Voto;
import com.votacao.enums.OpcaoVoto;
import com.votacao.exception.SessaoEncerradaException;
import com.votacao.exception.VotoDuplicadoException;
import com.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VotoService")
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;
    @Mock
    private SessaoVotacaoService sessaoService;

    @InjectMocks
    private VotoService votoService;

    private SessaoVotacao sessaoAtiva;
    private SessaoVotacao sessaoEncerrada;
    private UUID sessaoId;

    @BeforeEach
    void setUp() {
        sessaoId = UUID.randomUUID();

        Pauta pauta = Pauta.builder()
            .id(UUID.randomUUID())
            .titulo("Pauta Teste")
            .criadaEm(LocalDateTime.now())
            .build();

        sessaoAtiva = SessaoVotacao.builder()
            .id(sessaoId)
            .pauta(pauta)
            .abertura(LocalDateTime.now().minusMinutes(1))
            .encerramento(LocalDateTime.now().plusHours(1))
            .build();

        sessaoEncerrada = SessaoVotacao.builder()
            .id(sessaoId)
            .pauta(pauta)
            .abertura(LocalDateTime.now().minusMinutes(10))
            .encerramento(LocalDateTime.now().minusMinutes(5))
            .build();
    }

    @Test
    @DisplayName("deve registrar voto SIM com sucesso")
    void deveRegistrarVotoSim() {
        RegistrarVotoRequest request = new RegistrarVotoRequest("12345678901", OpcaoVoto.SIM);
        Voto votoSalvo = Voto.builder()
            .id(UUID.randomUUID())
            .sessao(sessaoAtiva)
            .associadoId("12345678901")
            .voto(OpcaoVoto.SIM)
            .registradoEm(LocalDateTime.now())
            .build();

        when(sessaoService.getSessao(sessaoId)).thenReturn(sessaoAtiva);
        when(votoRepository.existsBySessaoIdAndAssociadoId(sessaoId, "12345678901")).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenReturn(votoSalvo);

        VotoResponse response = votoService.registrar(sessaoId, request);

        assertThat(response).isNotNull();
        assertThat(response.voto()).isEqualTo(OpcaoVoto.SIM);
        assertThat(response.associadoId()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("deve lançar exceção quando sessão está encerrada")
    void deveLancarExcecaoQuandoSessaoEncerrada() {
        RegistrarVotoRequest request = new RegistrarVotoRequest("12345678901", OpcaoVoto.SIM);
        when(sessaoService.getSessao(sessaoId)).thenReturn(sessaoEncerrada);

        assertThatThrownBy(() -> votoService.registrar(sessaoId, request))
            .isInstanceOf(SessaoEncerradaException.class);
    }

    @Test
    @DisplayName("deve lançar exceção quando associado já votou")
    void deveLancarExcecaoQuandoAssociadoJaVotou() {
        RegistrarVotoRequest request = new RegistrarVotoRequest("12345678901", OpcaoVoto.NAO);
        when(sessaoService.getSessao(sessaoId)).thenReturn(sessaoAtiva);
        when(votoRepository.existsBySessaoIdAndAssociadoId(sessaoId, "12345678901")).thenReturn(true);

        assertThatThrownBy(() -> votoService.registrar(sessaoId, request))
            .isInstanceOf(VotoDuplicadoException.class)
            .hasMessageContaining("12345678901");
    }

    @Test
    @DisplayName("deve registrar voto NAO com sucesso")
    void deveRegistrarVotoNao() {
        RegistrarVotoRequest request = new RegistrarVotoRequest("99988877766", OpcaoVoto.NAO);
        Voto votoSalvo = Voto.builder()
            .id(UUID.randomUUID())
            .sessao(sessaoAtiva)
            .associadoId("99988877766")
            .voto(OpcaoVoto.NAO)
            .registradoEm(LocalDateTime.now())
            .build();

        when(sessaoService.getSessao(sessaoId)).thenReturn(sessaoAtiva);
        when(votoRepository.existsBySessaoIdAndAssociadoId(sessaoId, "99988877766")).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenReturn(votoSalvo);

        VotoResponse response = votoService.registrar(sessaoId, request);

        assertThat(response.voto()).isEqualTo(OpcaoVoto.NAO);
    }
}
