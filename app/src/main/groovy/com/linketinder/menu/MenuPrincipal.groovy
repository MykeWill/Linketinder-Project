 package com.linketinder.menu

 import com.linketinder.model.Candidato
 import com.linketinder.model.Empresa
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
                case "3" -> cadastrarCandidato()
                case "4" -> cadastrarEmpresa()
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

     private void cadastrarCandidato() {
         println("\n--- Cadastro de Candidato ---")
         try {
             print "Nome: "
             String nome = scanner.nextLine()

             print "email: "
             String email = scanner.nextLine()

             print "CPF: "
             String cpf = scanner.nextLine()

             print "Idade: "
             int idade = Integer.parseInt(scanner.nextLine())

             print "Estado (UF): "
             String estado = scanner.nextLine()

             print "CEP: "
             String cep = scanner.nextLine()

             print "Descrição: "
             String descricao = scanner.nextLine()

             print "Competências (separadas por vírgula): "
             List<String> competencias = scanner.nextLine().split(",").collect { it.trim() }

             Candidato candidato = new Candidato(nome, email, cpf, idade, estado, cep, descricao, competencias)

             candidatoService.cadastrarCandidatoService(candidato)

             println "Candidato cadastrado com sucesso!"

         }catch (NumberFormatException e) {
             println "Erro: Idade deve ser um número válido."
         } catch (IllegalArgumentException e) {
             println "Erro ao cadastrar candidato: ${e.getMessage()}"
         } catch (Exception e) {
             println "Ocorreu um erro inesperado: ${e.getMessage()}"
         }
     }

     private void cadastrarEmpresa() {
         println "\n--- Cadastro de Empresa ---"
         try {
             print "Nome: "
             String nome = scanner.nextLine()

             print "E-mail corporativo: "
             String email = scanner.nextLine()

             print "CNPJ (somente números): "
             String cnpj = scanner.nextLine()

             print "País: "
             String pais = scanner.nextLine()

             print "Estado (UF): "
             String estado = scanner.nextLine()

             print "CEP: "
             String cep = scanner.nextLine()

             print "Descrição: "
             String descricao = scanner.nextLine()

             print "Competências buscadas (separadas por vírgula): "
             List<String> competencias = scanner.nextLine().split(",").collect { it.trim() }

             Empresa empresa = new Empresa(nome, email, cnpj, pais, estado, cep, descricao, competencias)
             empresaService.cadastrarEmpresaService(empresa)
             println "✅ Empresa cadastrada com sucesso!"

         } catch (IllegalArgumentException e) {
             println "❌ Erro ao cadastrar empresa: ${e.getMessage()}"
         } catch (Exception e) {
             println "❌ Ocorreu um erro inesperado: ${e.getMessage()}"
         }
     }


 }