package com.linketinder.model

class Vaga {
    Integer id
    Integer empresaId
    String nome
    String descricao
    String local
    List<String> competencias

    Vaga(Integer empresaId, String nome, String descricao, String local, List<String> competencias) {
        this.empresaId = empresaId
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
