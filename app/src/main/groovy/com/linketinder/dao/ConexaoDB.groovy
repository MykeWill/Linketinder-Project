package com.linketinder.dao

import java.sql.Connection

class ConexaoDB {

    private static final ConnectionFactory FACTORY = PostgresConnectionFactory.getInstance()

    private ConexaoDB() {}

    static Connection obterConexao() {
        return FACTORY.criarConexao()
    }

    static <T> T executar(Closure<T> bloco) {
        Connection conexao = obterConexao()
        try {
            return bloco(conexao)
        } finally {
            conexao.close()
        }
    }
}