INSERT INTO candidato (nome, email, cpf, idade, estado, cep, descricao, senha)
VALUES ('Sandubinha', 'sandu@email.com', '123.456.789-01', 29, 'SP', '01001-000', 'Amo programar e fazer sanduíches', 'senha123'),
       ('Maria', 'maria.o@email.com', '987.654.321-02', 34, 'RJ', '20040-020', 'Especialista em Java e Spring', 'maria2024'),
       ('João', 'joao.p@email.com', '456.789.123-03', 36, 'Lisboa', '1000-001', 'Dev fullstack com foco em Angular', 'joaopass'),
       ('Ana', 'ana.c@email.com', '321.654.987-04', 31, 'DF', '70040-900', 'Analista de dados e Python', 'anac123'),
       ('Carlos', 'carlos.m@email.com', '159.753.486-05', 39, 'PR', '80010-000', 'Arquiteto de software e líder técnico', 'carlos2023');

INSERT INTO empresa (nome, cnpj, email, descricao, pais, estado, cep, senha)
VALUES ('Pastelsoft', '12.345.678/0001-99', 'recrutamento@pastelsoft.com', 'Especializada em ERPs para redes de restaurantes e bebidas', 'Brasil', 'SP', '01001-000', 'pastel123'),
       ('TechSolutions', '98.765.432/0001-88', 'rh@techsolutions.com', 'Consultoria em tecnologia e inovação', 'Brasil', 'RJ', '20040-020', 'tech456'),
       ('DevPower', '11.222.333/0001-77', 'contato@devpower.com', 'Desenvolvimento de sistemas para o varejo', 'Portugal', 'Lisboa', '1000-002', 'devpower'),
       ('DataMind', '44.555.666/0001-66', 'jobs@datamind.com', 'Inteligência artificial e big data', 'Brasil', 'DF', '70040-900', 'datamind'),
       ('InovaTech', '77.888.999/0001-55', 'carreiras@inovatech.com', 'Startup de fintechs e soluções bancárias', 'Brasil', 'PR', '80010-000', 'inovatech');

INSERT INTO competencia (nome)
VALUES ('Python'),
       ('Java'),
       ('Groovy'),
       ('Angular'),
       ('Spring Framework'),
       ('React'),
       ('Node.js'),
       ('Ilustrador'),
       ('Carpinteiro'),
       ('SQL'),
       ('AWS'),
       ('Docker'),
       ('Kubernetes'),
       ('Machine Learning'),
       ('Data Science');

INSERT INTO vaga (empresa_id, nome, descricao, local)
VALUES (1, 'Desenvolvedor Spring', 'Backend com Spring Boot e microserviços', 'São Paulo - SP'),
       (1, 'Engenheiro de Software ERP', 'Manutenção e evolução do ERP Pastelsoft', 'São Paulo - SP'),
       (2, 'Analista de Sistemas', 'Levantamento de requisitos e arquitetura de soluções', 'Rio de Janeiro - RJ'),
       (3, 'Dev Frontend Angular', 'Desenvolvimento de interfaces para sistemas de varejo', 'Lisboa, Portugal'),
       (4, 'Cientista de Dados', 'Modelagem e análise de grandes volumes de dados', 'Brasília - DF'),
       (5, 'DevOps Engineer', 'Automação e infraestrutura em cloud', 'Curitiba - PR');

INSERT INTO candidato_competencia (candidato_id, competencia_id)
VALUES (1, 3),
       (1, 5),
       (1, 9),
       (2, 1),
       (2, 5),
       (2, 10),
       (3, 4),
       (3, 6),
       (3, 7),
       (4, 1),
       (4, 14),
       (4, 15),
       (5, 2),
       (5, 5),
       (5, 12);

INSERT INTO vaga_competencia (vaga_id, competencia_id)
VALUES (1, 2),
       (1, 5),
       (1, 10),
       (2, 2),
       (2, 5),
       (3, 4),
       (3, 6),
       (3, 7),
       (4, 4),
       (4, 6),
       (5, 1),
       (5, 14),
       (5, 15),
       (6, 11),
       (6, 12),
       (6, 13);

INSERT INTO curtida_candidato_vaga (candidato_id, vaga_id)
VALUES (1, 1),
       (1, 2),
       (2, 5),
       (3, 3),
       (4, 5),
       (5, 1);

INSERT INTO curtida_empresa_candidato (empresa_id, candidato_id)
VALUES (1, 1),
       (1, 5),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5);