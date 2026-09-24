package com.linketinder.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CompetenciaDao {

    Integer buscarIdPorNome(String nome) {
        String sql = 'SELECT id FROM competencia WHERE nome = ?'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, nome)
            ResultSet resultado = stmt.executeQuery()
            if (resultado.next()) {
                return resultado.getInt('id')
            }
            return null
        } finally {
            conexao.close()
        }
    }

    Integer inserir(String nome) {
        String sql = 'INSERT INTO competencia (nome) VALUES (?) RETURNING id'
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, nome)
            ResultSet resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getInt('id')
        } finally {
            conexao.close()
        }
    }

    Integer buscarOuCriar(String nome) {
        Integer id = buscarIdPorNome(nome)
        if (id != null) {
            return id
        }
        return inserir(nome)
    }
}