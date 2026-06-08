package com.votacao.entity;

import com.votacao.enums.OpcaoVoto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "votos",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_voto_sessao_associado",
        columnNames = {"sessao_id", "associado_id"}
    ),
    indexes = {
        @Index(name = "idx_votos_sessao_id", columnList = "sessao_id"),
        @Index(name = "idx_votos_associado_id", columnList = "associado_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoVotacao sessao;

    @Column(name = "associado_id", nullable = false)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpcaoVoto voto;

    @Column(name = "registrado_em", nullable = false, updatable = false)
    private LocalDateTime registradoEm;

    @PrePersist
    protected void prePersist() {
        registradoEm = LocalDateTime.now();
    }
}
