package com.votacao.config;

import com.votacao.dto.request.AbrirSessaoRequest;
import com.votacao.dto.request.CriarPautaRequest;
import com.votacao.dto.request.RegistrarVotoRequest;
import com.votacao.entity.Pauta;
import com.votacao.entity.SessaoVotacao;
import com.votacao.entity.Voto;
import com.votacao.enums.OpcaoVoto;
import com.votacao.repository.PautaRepository;
import com.votacao.repository.SessaoVotacaoRepository;
import com.votacao.repository.VotoRepository;
import com.votacao.service.PautaService;
import com.votacao.service.SessaoVotacaoService;
import com.votacao.service.VotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoService;
    private final VotoService votoService;
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (pautaRepository.count() > 0) {
            log.info("Banco já populado, pulando inicialização.");
            return;
        }

        log.info("Populando banco com dados de exemplo...");

        sessaoAtiva();
        sessaoAtivaVazia();
        semSessao();
        sessaoEncerrada();

        log.info("Dados de exemplo criados.");
    }

    private void sessaoAtiva() {
        var pauta = pautaService.criar(new CriarPautaRequest(
            "Aprovação do orçamento 2025",
            "Votação do orçamento anual da cooperativa para o exercício de 2025"
        ));
        var sessao = sessaoService.abrir(pauta.id(), new AbrirSessaoRequest(60));

        votar(sessao.id(), "assoc-001", OpcaoVoto.SIM);
        votar(sessao.id(), "assoc-002", OpcaoVoto.SIM);
        votar(sessao.id(), "assoc-003", OpcaoVoto.SIM);
        votar(sessao.id(), "assoc-004", OpcaoVoto.NAO);
        votar(sessao.id(), "assoc-005", OpcaoVoto.SIM);
        votar(sessao.id(), "assoc-006", OpcaoVoto.NAO);
        votar(sessao.id(), "assoc-007", OpcaoVoto.SIM);
    }

    private void sessaoAtivaVazia() {
        var pauta = pautaService.criar(new CriarPautaRequest(
            "Eleição da nova diretoria",
            "Escolha dos membros da diretoria para o mandato 2025-2027"
        ));
        sessaoService.abrir(pauta.id(), new AbrirSessaoRequest(120));
    }

    private void semSessao() {
        pautaService.criar(new CriarPautaRequest(
            "Reforma do estatuto social",
            "Proposta de atualização do estatuto para adequação à legislação vigente"
        ));
        pautaService.criar(new CriarPautaRequest(
            "Aquisição de novos equipamentos",
            "Compra de maquinário para ampliação da capacidade produtiva"
        ));
    }

    private void sessaoEncerrada() {
        var pauta = pautaRepository.save(Pauta.builder()
            .titulo("Plano de expansão 2024")
            .descricao("Aprovação do plano de expansão para o exercício anterior")
            .build());

        var sessao = sessaoRepository.save(SessaoVotacao.builder()
            .pauta(pauta)
            .abertura(LocalDateTime.now().minusHours(2))
            .encerramento(LocalDateTime.now().minusMinutes(30))
            .build());

        salvarVoto(sessao, "assoc-020", OpcaoVoto.SIM);
        salvarVoto(sessao, "assoc-021", OpcaoVoto.SIM);
        salvarVoto(sessao, "assoc-022", OpcaoVoto.SIM);
        salvarVoto(sessao, "assoc-023", OpcaoVoto.NAO);
        salvarVoto(sessao, "assoc-024", OpcaoVoto.NAO);
        salvarVoto(sessao, "assoc-025", OpcaoVoto.SIM);
    }

    private void votar(UUID sessaoId, String associadoId, OpcaoVoto opcao) {
        votoService.registrar(sessaoId, new RegistrarVotoRequest(associadoId, opcao));
    }

    private void salvarVoto(SessaoVotacao sessao, String associadoId, OpcaoVoto opcao) {
        votoRepository.save(Voto.builder()
            .sessao(sessao)
            .associadoId(associadoId)
            .voto(opcao)
            .build());
    }
}
