package com.linketinder.menu

import com.linketinder.controller.EmpresaController
import com.linketinder.model.Empresa

class EmpresaView {

    private EmpresaController empresaController
    private Scanner scanner

    EmpresaView(EmpresaController empresaController, Scanner scanner) {
        this.empresaController = empresaController
        this.scanner = scanner
    }

    void listar() {
        println "\n--- Empresas cadastradas ---"
        empresaController.listarTodasEmpresasController().each { empresa ->
            println empresa.exibirDetalhes()
        }
    }

    void cadastrar() {
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

            print "Senha: "
            String senha = scanner.nextLine()

            Empresa empresa = new Empresa(nome, email, cnpj, pais, estado, cep, descricao, senha)
            empresaController.cadastrarEmpresaController(empresa)
            println "✅ Empresa cadastrada com sucesso!"

        } catch (IllegalArgumentException e) {
            println "Erro ao cadastrar empresa: ${e.getMessage()}"
        } catch (Exception e) {
            println "Ocorreu um erro inesperado: ${e.getMessage()}"
        }
    }
}