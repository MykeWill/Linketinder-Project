package com.linketinder.model

import java.time.LocalDate

class Recrutador {
    Integer id
    Integer empresaId
    String nome
    String areaRecrutamento
    String descricao
    String email
    String cpf
    LocalDate dataNascimento
    String senhaHash

    Recrutador(Integer empresaId, String nome, String areaRecrutamento, String descricao,
               String email, String cpf, LocalDate dataNascimento, String senhaHash) {
        this.empresaId = empresaId
        this.nome = nome
        this.areaRecrutamento = areaRecrutamento
        this.descricao = descricao
        this.email = email
        this.cpf = cpf
        this.dataNascimento = dataNascimento
        this.senhaHash = senhaHash
    }
}
