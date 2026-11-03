BEGIN;

INSERT INTO empresa (nome, cnpj, email, descricao, pais, estado, cep, senha)
VALUES
    ('TechNova', '12.345.678/0001-95', 'contato@technova.com',
     'Empresa de tecnologia e desenvolvimento de software', 'Brasil', 'SP', '01001-000',
     '$2a$10$Cs529vUTsomZfvWSaBG1G.067fcbzGzRJCV9BfJ/TUI9o3St8WTXa'),
    ('Padaria Central', '98.765.432/0001-98', 'contato@padaria.com',
     'Padaria com sistemas próprios para gestão e atendimento', 'Brasil', 'SP', '01310-100',
     '$2a$10$Cs529vUTsomZfvWSaBG1G.067fcbzGzRJCV9BfJ/TUI9o3St8WTXa');

INSERT INTO candidato (nome, email, cpf, idade, estado, cep, descricao, senha)
VALUES
    ('Ana Souza', 'ana@email.com', '52998224725', 28, 'SP', '01001-000',
     'Profissional com experiência em desenvolvimento backend e bancos de dados',
     '$2a$10$Cs529vUTsomZfvWSaBG1G.067fcbzGzRJCV9BfJ/TUI9o3St8WTXa'),
    ('Bruno Lima', 'bruno@email.com', '11144477735', 31, 'SP', '01310-100',
     'Profissional com experiência em desenvolvimento frontend',
     '$2a$10$Cs529vUTsomZfvWSaBG1G.067fcbzGzRJCV9BfJ/TUI9o3St8WTXa');

INSERT INTO recrutador
    (empresa_id, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha_hash)
SELECT e.id, dados.nome, dados.area_recrutamento, dados.descricao, dados.email,
       dados.cpf, dados.data_nascimento::DATE, dados.senha_hash
FROM (VALUES
    ('TechNova', 'Carlos (TechNova)', 'Tecnologia',
     'Recrutador da equipe TechNova', 'carlos@technova.com', '93541134780',
     '1985-04-12', '$2a$10$Cs529vUTsomZfvWSaBG1G.067fcbzGzRJCV9BfJ/TUI9o3St8WTXa'),
    ('Padaria Central', 'Mariana (Padaria)', 'Tecnologia',
     'Recrutadora da equipe Padaria Central', 'mariana@padaria.com', '12345678909',
     '1988-09-23', '$2a$10$Cs529vUTsomZfvWSaBG1G.067fcbzGzRJCV9BfJ/TUI9o3St8WTXa')
) AS dados(empresa_nome, nome, area_recrutamento, descricao, email, cpf, data_nascimento, senha_hash)
JOIN empresa e ON e.nome = dados.empresa_nome;

INSERT INTO competencia (nome)
VALUES ('Java'), ('SQL'), ('Angular'), ('React'), ('Python');

INSERT INTO vaga (empresa_id, recrutador_id, nome, descricao, local)
SELECT e.id, r.id, dados.nome, dados.descricao, dados.local
FROM (VALUES
    ('contato@technova.com', 'carlos@technova.com', 'Desenvolvedor Backend',
     'Desenvolvimento e manutenção de serviços backend', 'São Paulo - SP'),
    ('contato@padaria.com', 'mariana@padaria.com', 'Desenvolvedor Frontend',
     'Desenvolvimento de interfaces para os sistemas da empresa', 'São Paulo - SP')
) AS dados(empresa_email, recrutador_email, nome, descricao, local)
JOIN empresa e ON e.email = dados.empresa_email
JOIN recrutador r ON r.email = dados.recrutador_email;

INSERT INTO candidato_competencia (candidato_id, competencia_id)
SELECT c.id, comp.id
FROM (VALUES
    ('ana@email.com', 'Java'),
    ('ana@email.com', 'SQL'),
    ('bruno@email.com', 'Angular'),
    ('bruno@email.com', 'React')
) AS dados(candidato_email, competencia_nome)
JOIN candidato c ON c.email = dados.candidato_email
JOIN competencia comp ON comp.nome = dados.competencia_nome;

INSERT INTO vaga_competencia (vaga_id, competencia_id)
SELECT v.id, comp.id
FROM (VALUES
    ('contato@technova.com', 'Desenvolvedor Backend', 'Java'),
    ('contato@technova.com', 'Desenvolvedor Backend', 'SQL'),
    ('contato@technova.com', 'Desenvolvedor Backend', 'Python'),
    ('contato@padaria.com', 'Desenvolvedor Frontend', 'Angular'),
    ('contato@padaria.com', 'Desenvolvedor Frontend', 'React')
) AS dados(empresa_email, vaga_nome, competencia_nome)
JOIN empresa e ON e.email = dados.empresa_email
JOIN vaga v ON v.empresa_id = e.id AND v.nome = dados.vaga_nome
JOIN competencia comp ON comp.nome = dados.competencia_nome;

COMMIT;
