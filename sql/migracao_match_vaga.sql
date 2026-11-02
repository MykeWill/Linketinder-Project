BEGIN;

ALTER TABLE curtida_empresa_candidato
    ADD COLUMN vaga_id INTEGER;

UPDATE curtida_empresa_candidato cec
SET vaga_id = (
    SELECT MIN(v.id)
    FROM vaga v
    WHERE v.empresa_id = cec.empresa_id
);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM curtida_empresa_candidato
        WHERE vaga_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'Migration aborted: at least one company like has no existing vacancy to associate.';
    END IF;
END;
$$;

ALTER TABLE curtida_empresa_candidato
    ADD CONSTRAINT curtida_empresa_candidato_vaga_id_fkey
        FOREIGN KEY (vaga_id) REFERENCES vaga (id) ON DELETE CASCADE;

ALTER TABLE curtida_empresa_candidato
    DROP CONSTRAINT curtida_empresa_candidato_pkey;

ALTER TABLE curtida_empresa_candidato
    ADD CONSTRAINT curtida_empresa_candidato_pkey
        PRIMARY KEY (vaga_id, candidato_id);

ALTER TABLE curtida_empresa_candidato
    ALTER COLUMN vaga_id SET NOT NULL;

COMMIT;
