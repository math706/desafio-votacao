package com.votacao.service;

import com.votacao.dto.request.RegistrarVotoRequest;
import com.votacao.dto.response.VotoResponse;
import com.votacao.entity.Voto;
import com.votacao.exception.SessaoEncerradaException;
import com.votacao.exception.VotoDuplicadoException;
import com.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoService sessaoService;

    @Transactional
    public VotoResponse registrar(UUID sessaoId, RegistrarVotoRequest request) {
        var sessao = sessaoService.getSessao(sessaoId);

        if (!sessao.isAtiva()) {
            throw new SessaoEncerradaException(sessaoId);
        }
        if (votoRepository.existsBySessaoIdAndAssociadoId(sessaoId, request.associadoId())) {
            throw new VotoDuplicadoException(request.associadoId());
        }

        var voto = votoRepository.save(
            Voto.builder()
                .sessao(sessao)
                .associadoId(request.associadoId())
                .voto(request.voto())
                .build()
        );

        log.debug("Voto {} registrado por {} na sessão {}", request.voto(), request.associadoId(), sessaoId);
        return VotoResponse.from(voto);
    }
}
