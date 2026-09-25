CREATE TABLE candidato
(
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR(100)        NOT NULL,
    email     VARCHAR(150) UNIQUE NOT NULL,
    cpf       VARCHAR(14) UNIQUE  NOT NULL,
    idade     INTEGER             NOT NULL,
    estado    VARCHAR(100)        NOT NULL,
    cep       VARCHAR(20)         NOT NULL,
    descricao TEXT,
    senha     VARCHAR(255)        NOT NULL
);

CREATE TABLE empresa
(
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR(150)        NOT NULL,
    cnpj      VARCHAR(18) UNIQUE  NOT NULL,
    email     VARCHAR(150) UNIQUE NOT NULL,
    descricao TEXT,
    pais      VARCHAR(100)        NOT NULL,
    estado    VARCHAR(100)        NOT NULL,
    cep       VARCHAR(20)         NOT NULL,
    senha     VARCHAR(255)        NOT NULL
);

CREATE TABLE competencia
(
    id   SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE vaga
(
    id         SERIAL PRIMARY KEY,
    empresa_id INTEGER      NOT NULL REFERENCES empresa (id) ON DELETE CASCADE,
    nome       VARCHAR(150) NOT NULL,
    descricao  TEXT,
    local      VARCHAR(200) NOT NULL
);

CREATE TABLE candidato_competencia
(
    candidato_id   INTEGER NOT NULL REFERENCES candidato (id) ON DELETE CASCADE,
    competencia_id INTEGER NOT NULL REFERENCES competencia (id) ON DELETE CASCADE,
    PRIMARY KEY (candidato_id, competencia_id)
);

CREATE TABLE vaga_competencia
(
    vaga_id        INTEGER NOT NULL REFERENCES vaga (id) ON DELETE CASCADE,
    competencia_id INTEGER NOT NULL REFERENCES competencia (id) ON DELETE CASCADE,
    PRIMARY KEY (vaga_id, competencia_id)
);

CREATE TABLE curtida_candidato_vaga
(
    candidato_id INTEGER NOT NULL REFERENCES candidato (id) ON DELETE CASCADE,
    vaga_id      INTEGER NOT NULL REFERENCES vaga (id) ON DELETE CASCADE,
    data_hora    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (candidato_id, vaga_id)
);

CREATE TABLE curtida_empresa_candidato
(
    empresa_id   INTEGER NOT NULL REFERENCES empresa (id) ON DELETE CASCADE,
    candidato_id INTEGER NOT NULL REFERENCES candidato (id) ON DELETE CASCADE,
    data_hora    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (empresa_id, candidato_id)
);