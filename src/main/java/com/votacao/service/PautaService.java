package com.votacao.service;

import com.votacao.dto.request.CriarPautaRequest;
import com.votacao.dto.response.PautaResponse;
import com.votacao.entity.Pauta;
import com.votacao.exception.PautaNaoEncontradaException;
import com.votacao.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;

    @Transactional
    public PautaResponse criar(CriarPautaRequest request) {
        var pauta = pautaRepository.save(
            Pauta.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .build()
        );
        log.info("Pauta criada: {} [{}]", pauta.getTitulo(), pauta.getId());
        return PautaResponse.from(pauta);
    }

    @Transactional(readOnly = true)
    public List<PautaResponse> listar() {
        return pautaRepository.findAll().stream()
            .map(PautaResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public PautaResponse buscarPorId(UUID id) {
        return pautaRepository.findById(id)
            .map(PautaResponse::from)
            .orElseThrow(() -> new PautaNaoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public Pauta getPauta(UUID id) {
        return pautaRepository.findById(id)
            .orElseThrow(() -> new PautaNaoEncontradaException(id));
    }
}
