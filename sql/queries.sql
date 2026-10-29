-- candidatos disponíveis para recrutador (pré-match)
-- $1: recrutador_id
SELECT c.id,
       c.descricao,
       array_agg(comp.nome) AS competencias
FROM candidato c
         JOIN candidato_competencia cc ON c.id = cc.candidato_id
         JOIN competencia comp ON cc.competencia_id = comp.id
WHERE NOT EXISTS (
    SELECT 1
    FROM recrutador r
             JOIN curtida_empresa_candidato cec ON cec.empresa_id = r.empresa_id
    WHERE r.id = $1
      AND cec.candidato_id = c.id
)
GROUP BY c.id
ORDER BY c.id;

-- vagas visíveis para recrutador
-- $1: empresa_id; $2: recrutador_id
SELECT v.id,
       v.empresa_id,
       v.recrutador_id,
       v.nome,
       v.descricao,
       v.local,
       array_agg(comp.nome) AS competencias
FROM vaga v
         LEFT JOIN vaga_competencia vc ON vc.vaga_id = v.id
         LEFT JOIN competencia comp ON comp.id = vc.competencia_id
WHERE v.empresa_id = $1
   OR v.recrutador_id = $2
GROUP BY v.id
ORDER BY v.id;

-- perfil do candidato para recrutador (pós-match)
-- $1: recrutador_id
SELECT c.nome,
       c.idade,
       c.email,
       c.estado,
       c.descricao,
       array_agg(comp.nome) AS competencias
FROM candidato c
         JOIN curtida_candidato_vaga ccv ON ccv.candidato_id = c.id
         JOIN vaga v ON v.id = ccv.vaga_id
         JOIN recrutador r ON r.id = $1 AND r.empresa_id = v.empresa_id
         JOIN curtida_empresa_candidato cec
              ON cec.empresa_id = r.empresa_id AND cec.candidato_id = c.id
         JOIN candidato_competencia cc ON cc.candidato_id = c.id
         JOIN competencia comp ON comp.id = cc.competencia_id
GROUP BY c.id
ORDER BY c.nome;

-- vagas visíveis para candidato (pré-match)
SELECT v.nome AS vaga_nome,
       v.descricao AS vaga_descricao,
       array_agg(comp.nome) AS competencias_exigidas
FROM vaga v
         LEFT JOIN vaga_competencia vc ON vc.vaga_id = v.id
         LEFT JOIN competencia comp ON comp.id = vc.competencia_id
GROUP BY v.id
ORDER BY v.id;

-- matches e contatos liberados (pós-match)
SELECT e.nome AS empresa_nome,
       e.descricao AS empresa_descricao,
       e.email AS empresa_email,
       r.nome AS recrutador_nome,
       r.area_recrutamento,
       r.email AS recrutador_email,
       c.email AS candidato_email
FROM curtida_candidato_vaga ccv
         JOIN vaga v ON ccv.vaga_id = v.id
         JOIN empresa e ON v.empresa_id = e.id
         JOIN curtida_empresa_candidato cec
              ON cec.empresa_id = e.id AND cec.candidato_id = ccv.candidato_id
         JOIN candidato c ON c.id = ccv.candidato_id
         LEFT JOIN recrutador r ON r.id = cec.recrutador_id
ORDER BY e.nome, c.email;