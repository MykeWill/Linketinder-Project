package com.linketinder.dao

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class PostgresConnectionFactory implements ConnectionFactory {

    private static PostgresConnectionFactory instancia

    private PostgresConnectionFactory() {}

    static synchronized PostgresConnectionFactory getInstance() {
        if (instancia == null) {
            instancia = new PostgresConnectionFactory()
        }
        return instancia
    }

    @Override
    Connection criarConexao() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUsuario(),
                DatabaseConfig.getSenha()
        )
    }
}