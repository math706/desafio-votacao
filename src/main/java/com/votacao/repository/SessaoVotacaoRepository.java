package com.votacao.repository;

import com.votacao.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, UUID> {

    boolean existsByPautaId(UUID pautaId);

    Optional<SessaoVotacao> findByPautaId(UUID pautaId);
}
