package com.linketinder.repository

import com.linketinder.model.Empresa

 class EmpresaRepository {

    private List<Empresa> empresas = []

    EmpresaRepository() {
        inicializarDados()
    }

    private void inicializarDados() {
        empresas.add(
                new Empresa(
                        "Arroz-Gostoso",
                        "contato@arrozgostoso.com",
                        "11.111.111/0001-11",
                        "Brasil",
                        "SP",
                        "01000-000",
                        "Empresa do ramo alimentício.",
                        ["Java", "Spring Framework"]
                )
        )

        empresas.add(
                new Empresa(
                        "Império do Boliche",
                        "rh@imperiodoboliche.com",
                        "22.222.222/0001-22",
                        "Brasil",
                        "RJ",
                        "20000-000",
                        "Rede de entretenimento e lazer.",
                        ["Angular", "JavaScript"]
                )
        )

        empresas.add(
                new Empresa(
                        "TechNova",
                        "vagas@technova.com",
                        "33.333.333/0001-33",
                        "Brasil",
                        "MG",
                        "30000-000",
                        "Startup de soluções em nuvem.",
                        ["Python", "SQL"]))

        empresas.add(
                new Empresa(
                        "QualiTest Corp",
                        "recrutamento@qualitest.com",
                        "44.444.444/0001-44",
                        "Brasil",
                        "PR", "80000-000",
                        "Empresa especializada em qualidade de software.",
                        ["Python", "Selenium"]))

        empresas.add(
                new Empresa(
                        "DevBridge",
                        "contato@devbridge.com",
                        "55.555.555/0001-55",
                        "Brasil",
                        "BA",
                        "40000-000",
                        "Consultoria em desenvolvimento full stack.",
                        ["Java", "Angular", "SQL"]))
    }

    List<Empresa> listarTodasEmpresasRepository() {
        empresas
    }

    void adicionarEmpresaRepository(Empresa empresa) {
        empresas.add(empresa)
    }
 }