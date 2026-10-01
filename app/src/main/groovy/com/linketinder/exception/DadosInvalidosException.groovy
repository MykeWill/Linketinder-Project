package com.linketinder.exception

class DadosInvalidosException extends RuntimeException {
    DadosInvalidosException(String mensagem) {
        super(mensagem)
    }
}