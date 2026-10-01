package com.linketinder.menu

class MenuPrincipal {

    private CandidatoView candidatoView
    private EmpresaView empresaView
    private Scanner scanner

    MenuPrincipal(CandidatoView candidatoView, EmpresaView empresaView) {
        this.candidatoView = candidatoView
        this.empresaView = empresaView
        this.scanner = new Scanner(System.in)
    }

    void iniciar() {
        boolean continuar = true
        while (continuar) {
            exibirOpcoes()
            String opcao = scanner.nextLine().trim()

            switch (opcao) {
                case "1" -> candidatoView.listar()
                case "2" -> empresaView.listar()
                case "3" -> candidatoView.cadastrar()
                case "4" -> empresaView.cadastrar()
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
        3 - Cadastrar candidato
        4 - Cadastrar empresa
        0 - Sair
        ========================
        Escolha uma opção:""".stripIndent()
    }
}