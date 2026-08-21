 package com.linketinder.menu

 import com.linketinder.service.CandidatoService
 import com.linketinder.service.EmpresaService

 class MenuPrincipal {

    private CandidatoService candidatoService
    private EmpresaService empresaService
    private Scanner scanner

    MenuPrincipal(CandidatoService candidatoService, EmpresaService empresaService) {
        this.candidatoService = candidatoService
        this.empresaService = empresaService
        this.scanner = new Scanner(System.in)
    }

    void iniciar() {
        boolean continuar = true
        while (continuar) {
            exibirOpcoes()
            String opcao = scanner.nextLine().trim()

            switch (opcao) {
                case "1" -> listarCandidatos()
                case "2" -> listarEmpresas()
                case "0" -> {
                    continuar = false
                    println "Encerrando o Linketinder. Até logo!"
                }
                default -> println "Opção inválida, tente novamente."
            }
        }
    }

    private void exibirOpcoes() {
        println """
        ===== Linketinder =====
        1 - Listar candidatos
        2 - Listar empresas
        0 - Sair
        ========================
        Escolha uma opção:""".stripIndent()
    }

    private void listarCandidatos() {
        println "\n--- Candidatos cadastrados ---"
        candidatoService.listarTodosCandidatosService().each { candidato ->
            println candidato.exibirDetalhes()
        }
    }

    private void listarEmpresas() {
        println "\n--- Empresas cadastradas ---"
        empresaService.listarTodasEmpresasService().each { empresa ->
            println empresa.exibirDetalhes()
        }
    }
 }