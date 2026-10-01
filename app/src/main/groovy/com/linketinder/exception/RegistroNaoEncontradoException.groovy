package com.linketinder.exception

class RegistroNaoEncontradoException extends RuntimeException {
    RegistroNaoEncontradoException(String mensagem) {
        super(mensagem)
    }
}