package com.linketinder.model

class Vaga {
    Integer id
    Integer empresaId
    Integer recrutadorId
    String nome
    String descricao
    String local
    List<String> competencias

    Vaga(Integer empresaId, String nome, String descricao, String local,
         List<String> competencias, Integer recrutadorId = null) {
        this.empresaId = empresaId
        this.recrutadorId = recrutadorId
        this.nome = nome
        this.descricao = descricao
        this.local = local
        this.competencias = competencias
    }

    @Override
    String toString() {
        """
        Vaga: ${nome}
        Descrição: ${descricao}
        Local: ${local}
        Competências exigidas: ${competencias.join(', ')}
        """.stripIndent()
    }
}
