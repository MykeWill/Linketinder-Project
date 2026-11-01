package com.linketinder.model

class Match {
    String empresaNome
    String empresaDescricao
    String empresaEmail
    String recrutadorNome
    String recrutadorAreaRecrutamento
    String recrutadorEmail
    String candidatoNome
    int candidatoIdade
    String candidatoEmail
    List<String> candidatoCompetencias
    String candidatoDescricao
    String candidatoEstado

    Match(String empresaNome, String empresaDescricao, String empresaEmail,
          String recrutadorNome, String recrutadorAreaRecrutamento, String recrutadorEmail,
          String candidatoNome, int candidatoIdade, String candidatoEmail,
          List<String> candidatoCompetencias, String candidatoDescricao, String candidatoEstado) {
        this.empresaNome = empresaNome
        this.empresaDescricao = empresaDescricao
        this.empresaEmail = empresaEmail
        this.recrutadorNome = recrutadorNome
        this.recrutadorAreaRecrutamento = recrutadorAreaRecrutamento
        this.recrutadorEmail = recrutadorEmail
        this.candidatoNome = candidatoNome
        this.candidatoIdade = candidatoIdade
        this.candidatoEmail = candidatoEmail
        this.candidatoCompetencias = candidatoCompetencias ?: []
        this.candidatoDescricao = candidatoDescricao
        this.candidatoEstado = candidatoEstado
    }
}
