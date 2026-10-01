package com.linketinder.exception

class RegistroDuplicadoException extends RuntimeException {
    RegistroDuplicadoException(String mensagem) {
        super(mensagem)
    }
}