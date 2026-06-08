CREATE TABLE sessoes_votacao (
    id           UUID      NOT NULL DEFAULT gen_random_uuid(),
    pauta_id     UUID      NOT NULL,
    abertura     TIMESTAMP NOT NULL,
    encerramento TIMESTAMP NOT NULL,
    CONSTRAINT pk_sessoes_votacao PRIMARY KEY (id),
    CONSTRAINT fk_sessoes_pauta FOREIGN KEY (pauta_id) REFERENCES pautas (id),
    CONSTRAINT uk_sessoes_pauta UNIQUE (pauta_id)
);

CREATE INDEX idx_sessoes_pauta_id ON sessoes_votacao (pauta_id);
