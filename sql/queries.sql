-- antes do match
SELECT c.id,
       c.descricao,
       array_agg(comp.nome) AS competencias
FROM candidato c
         JOIN candidato_competencia cc ON c.id = cc.candidato_id
         JOIN competencia comp ON cc.competencia_id = comp.id
GROUP BY c.id
ORDER BY c.id;

SELECT v.id,
       v.descricao,
       v.local,
       array_agg(comp.nome) AS competencias_exigidas
FROM vaga v
         LEFT JOIN vaga_competencia vc ON v.id = vc.vaga_id
         LEFT JOIN competencia comp ON vc.competencia_id = comp.id
GROUP BY v.id
ORDER BY v.id;

-- deu match
SELECT c.id,
       c.nome,
       c.email,
       c.cpf,
       c.idade,
       c.estado,
       c.cep,
       c.descricao,
       array_agg(comp.nome) AS competencias
FROM candidato c
         JOIN candidato_competencia cc ON c.id = cc.candidato_id
         JOIN competencia comp ON cc.competencia_id = comp.id
GROUP BY c.id
ORDER BY c.id;

SELECT v.id,
       v.nome               AS vaga,
       e.nome               AS empresa,
       v.local,
       v.descricao,
       array_agg(comp.nome) AS competencias_exigidas
FROM vaga v
         JOIN empresa e ON v.empresa_id = e.id
         LEFT JOIN vaga_competencia vc ON v.id = vc.vaga_id
         LEFT JOIN competencia comp ON vc.competencia_id = comp.id
GROUP BY v.id, e.nome
ORDER BY v.id;

-- dados gerais (match)
SELECT c.id                         AS candidato_id,
       c.nome                       AS nome_candidato,
       c.email                      AS email_candidato,
       e.id                         AS empresa_id,
       e.nome                       AS empresa,
       e.email                      AS email_empresa,
       v.id                         AS vaga_id,
       v.nome                       AS vaga,
       ccv.data_hora                AS data_curtida_candidato,
       cec.data_hora                AS data_curtida_empresa
FROM curtida_candidato_vaga ccv
         JOIN vaga v ON ccv.vaga_id = v.id
         JOIN empresa e ON v.empresa_id = e.id
         JOIN curtida_empresa_candidato cec
              ON cec.empresa_id = e.id AND cec.candidato_id = ccv.candidato_id
         JOIN candidato c ON c.id = ccv.candidato_id
ORDER BY e.nome, c.nome;