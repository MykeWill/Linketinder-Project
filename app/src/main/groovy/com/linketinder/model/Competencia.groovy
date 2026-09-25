package com.linketinder.model

class Competencia {
    Integer id
    String nome

    Competencia(String nome) {
        this.nome = nome
    }

    @Override
    String toString() {
        return nome
    }
}