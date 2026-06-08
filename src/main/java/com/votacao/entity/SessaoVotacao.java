package com.votacao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessoes_votacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(nullable = false, updatable = false)
    private LocalDateTime abertura;

    @Column(nullable = false)
    private LocalDateTime encerramento;

    @Transient
    public boolean isAtiva() {
        return LocalDateTime.now().isBefore(encerramento);
    }
}
