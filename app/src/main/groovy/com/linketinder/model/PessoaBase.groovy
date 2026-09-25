package com.linketinder.model

 abstract class PessoaBase implements Pessoa {
    String nome
    String email
    String estado
    String cep
    String descricao

    PessoaBase(String nome, String email, String estado, String cep, String descricao) {
        this.nome = nome
        this.email = email
        this.estado = estado
        this.cep = cep
        this.descricao = descricao
    }

     @Override
     String getNome() { nome }

     @Override
     String getEmail() { email }

     @Override
     String getEstado() { estado }

     @Override
     String getCep() { cep }

     @Override
     String getDescricao() { descricao }

 }
