package com.linketinder.dao

import java.sql.Connection
import java.sql.DriverManager

class ConexaoDB {

    private static final String URL = 'jdbc:postgresql://172.27.176.1:5432/linkertinder'
    private static final String USUARIO = 'postgres'
    private static final String SENHA = 'aluno'

    static Connection obterConexao() {
        return DriverManager.getConnection(URL, USUARIO, SENHA)
    }
}