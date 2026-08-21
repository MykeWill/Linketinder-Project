package com.linketinder.repository

import com.linketinder.model.Candidato

 class CandidatoRepository {

    private  List<Candidato> candidatos = []

    CandidatoRepository(){
        incializarDados()
    }

    private void inicializarDados() {
        candidatos.add(
                new Candidato(
                        "Ana Souza",
                        "ana.souza@email.com",
                        "111.111.111-11",
                        28,
                        "SP",
                        "01000-000",
                        "Desenvolvedora backend apaixonada por dados.",
                        ["Java", "Spring Framework", "SQL"]
                )
        )

        candidatos.add(
                new Candidato(
                        "Bruno Lima",
                        "bruno.lima@email.com",
                        "222.222.222-22",
                        24,
                        "RJ",
                        "20000-000",
                        "Front-end entusiasta de acessibilidade web.",
                        ["Angular", "JavaScript", "CSS"]
                )
        )


        candidatos.add(
                new Candidato(
                        "Carla Dias",
                        "carla.dias@email.com",
                        "333.333.333-33",
                        31,
                        "MG",
                        "30000-000",
                        "Especialista em automação de testes.",
                        ["Python", "Selenium", "Java"]
                )
        )

        candidatos.add(
                new Candidato(
                        "Diego Alves",
                        "diego.alves@email.com",
                        "444.444.444-44",
                        26,
                        "PR",
                        "80000-000",
                        "Full stack com foco em APIs REST.",
                        ["Java", "Spring Framework", "Angular"]
                )
        )

        candidatos.add(
                new Candidato(
                        "Elisa Prado",
                        "elisa.prado@email.com",
                        "555.555.555-55",
                        22,
                        "BA",
                        "40000-000",
                        "Recém-formada, buscando primeira oportunidade.",
                        ["Python", "SQL"]
                )
        )

    }

     List<Candidato> listarTodosCandidatosRepository() {
         candidatos
     }

     void adicionarCandidatoRepository (Candidato candidato) {
         candidatos.add(candidato)
     }

 }
