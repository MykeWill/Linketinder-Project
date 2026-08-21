package com.linketinder.model

 abstract class PessoaBase implements Pessoa {
    String nome
    String email
    String estado
    String cep
    String descricao
    List<String> competencias

    PessoaBase(String nome, String email, String estado, String cep, String descricao, List<String> competencias) {
        this.nome = nome
        this.email = email
        this.estado = estado
        this.cep = cep
        this.descricao = descricao
        this.competencias = competencias
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

     @Override
     List<String> getCompetencias() { competencias }

 }
