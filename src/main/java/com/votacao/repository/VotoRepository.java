package com.votacao.repository;

import com.votacao.entity.Voto;
import com.votacao.enums.OpcaoVoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VotoRepository extends JpaRepository<Voto, UUID> {

    boolean existsBySessaoIdAndAssociadoId(UUID sessaoId, String associadoId);

    long countBySessaoId(UUID sessaoId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.sessao.id = :sessaoId AND v.voto = :opcao")
    long countBySessaoIdAndVoto(@Param("sessaoId") UUID sessaoId, @Param("opcao") OpcaoVoto opcao);
}
