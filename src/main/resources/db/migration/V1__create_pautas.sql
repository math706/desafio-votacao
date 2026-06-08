CREATE TABLE pautas (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    titulo      VARCHAR(255) NOT NULL,
    descricao   TEXT,
    criada_em   TIMESTAMP    NOT NULL,
    CONSTRAINT pk_pautas PRIMARY KEY (id)
);
