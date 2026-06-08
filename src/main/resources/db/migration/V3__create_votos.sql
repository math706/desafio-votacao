CREATE TABLE votos (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    sessao_id    UUID         NOT NULL,
    associado_id VARCHAR(255) NOT NULL,
    voto         VARCHAR(3)   NOT NULL CHECK (voto IN ('SIM', 'NAO')),
    registrado_em TIMESTAMP   NOT NULL,
    CONSTRAINT pk_votos PRIMARY KEY (id),
    CONSTRAINT fk_votos_sessao FOREIGN KEY (sessao_id) REFERENCES sessoes_votacao (id),
    CONSTRAINT uk_voto_sessao_associado UNIQUE (sessao_id, associado_id)
);

CREATE INDEX idx_votos_sessao_id    ON votos (sessao_id);
CREATE INDEX idx_votos_associado_id ON votos (associado_id);
