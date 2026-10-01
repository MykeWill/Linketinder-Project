package com.linketinder.menu

import com.linketinder.controller.CandidatoController
import com.linketinder.model.Candidato

class CandidatoView {

    private CandidatoController candidatoController
    private Scanner scanner

    CandidatoView(CandidatoController candidatoController, Scanner scanner) {
        this.candidatoController = candidatoController
        this.scanner = scanner
    }

    void listar() {
        println "\n--- Candidatos cadastrados ---"
        candidatoController.listarTodosCandidatosController().each { candidato ->
            println candidato.exibirDetalhes()
        }
    }

    void cadastrar() {
        println("\n--- Cadastro de Candidato ---")
        try {
            print "Nome: "
            String nome = scanner.nextLine()

            print "E-mail: "
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

            print "Senha: "
            String senha = scanner.nextLine()

            print "Competências (separadas por vírgula): "
            List<String> competencias = scanner.nextLine().split(",").collect { it.trim() }

            Candidato candidato = new Candidato(nome, email, cpf, idade, estado, cep, descricao, senha, competencias)

            candidatoController.cadastrarCandidatoController(candidato)

            println "Candidato cadastrado com sucesso!"

        } catch (NumberFormatException e) {
            println "Erro: Idade deve ser um número válido."
        } catch (IllegalArgumentException e) {
            println "Erro ao cadastrar candidato: ${e.getMessage()}"
        } catch (Exception e) {
            println "Ocorreu um erro inesperado: ${e.getMessage()}"
        }
    }
}