package com.linketinder.model

interface Pessoa {
    String getNome()
    String getEmail()
    String getEstado()
    String getCep()
    String getDescricao()
    List<String> getCompetencias()
    String exibirDetalhes()
}