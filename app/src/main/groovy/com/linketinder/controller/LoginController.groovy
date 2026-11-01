package com.linketinder.controller

import com.linketinder.service.LoginService

class LoginController {

    private final LoginService loginService

    LoginController(LoginService loginService) {
        this.loginService = loginService
    }

    Map autenticar(String email, String senha, String tipo) {
        return loginService.autenticar(email, senha, tipo)
    }
}
