package com.linketinder.exception

class ErroBancoException extends RuntimeException {
    ErroBancoException(String mensagem, Throwable causa) {
        super(mensagem, causa)
    }
}