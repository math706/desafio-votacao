package com.votacao.service;

import com.votacao.dto.request.AbrirSessaoRequest;
import com.votacao.dto.response.ResultadoVotacaoResponse;
import com.votacao.dto.response.SessaoVotacaoResponse;
import com.votacao.entity.SessaoVotacao;
import com.votacao.enums.OpcaoVoto;
import com.votacao.exception.SessaoJaExisteException;
import com.votacao.exception.SessaoNaoEncontradaException;
import com.votacao.repository.SessaoVotacaoRepository;
import com.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;
    private final PautaService pautaService;

    @Value("${votacao.sessao.duracao-padrao-minutos:1}")
    private int duracaoPadraoMinutos;

    @Transactional
    public SessaoVotacaoResponse abrir(UUID pautaId, AbrirSessaoRequest request) {
        if (sessaoRepository.existsByPautaId(pautaId)) {
            throw new SessaoJaExisteException(pautaId);
        }

        var pauta = pautaService.getPauta(pautaId);
        var duracao = (request != null && request.duracaoMinutos() != null)
            ? request.duracaoMinutos()
            : duracaoPadraoMinutos;

        var abertura = LocalDateTime.now();
        var sessao = sessaoRepository.save(
            SessaoVotacao.builder()
                .pauta(pauta)
                .abertura(abertura)
                .encerramento(abertura.plusMinutes(duracao))
                .build()
        );

        log.info("Sessão {} aberta para '{}', encerra às {}", sessao.getId(), pauta.getTitulo(), sessao.getEncerramento());
        return SessaoVotacaoResponse.from(sessao);
    }

    @Transactional(readOnly = true)
    public SessaoVotacaoResponse buscarPorId(UUID sessaoId) {
        return sessaoRepository.findById(sessaoId)
            .map(SessaoVotacaoResponse::from)
            .orElseThrow(() -> new SessaoNaoEncontradaException(sessaoId));
    }

    @Transactional(readOnly = true)
    public SessaoVotacao getSessao(UUID sessaoId) {
        return sessaoRepository.findById(sessaoId)
            .orElseThrow(() -> new SessaoNaoEncontradaException(sessaoId));
    }

    @Transactional(readOnly = true)
    public ResultadoVotacaoResponse obterResultado(UUID pautaId) {
        pautaService.getPauta(pautaId);

        var sessao = sessaoRepository.findByPautaId(pautaId)
            .orElseThrow(() -> new SessaoNaoEncontradaException("Pauta não possui sessão de votação: " + pautaId));

        long votosSim  = votoRepository.countBySessaoIdAndVoto(sessao.getId(), OpcaoVoto.SIM);
        long votosNao  = votoRepository.countBySessaoIdAndVoto(sessao.getId(), OpcaoVoto.NAO);
        long total     = votosSim + votosNao;
        String resultado = calcularResultado(votosSim, votosNao);

        return new ResultadoVotacaoResponse(
            pautaId,
            sessao.getPauta().getTitulo(),
            sessao.getId(),
            sessao.getEncerramento(),
            sessao.isAtiva(),
            total,
            votosSim,
            votosNao,
            resultado
        );
    }

    private String calcularResultado(long sim, long nao) {
        if (sim > nao) return "APROVADA";
        if (nao > sim) return "REPROVADA";
        return "EMPATE";
    }
}
