package com.linketinder.dao

import com.linketinder.model.Competencia
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
        Connection conexao = ConexaoDB.obterConexao()
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

    List<Competencia> listarTodas() {
        String sql = 'SELECT id, nome FROM competencia ORDER BY nome'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet resultado = stmt.executeQuery()
            List<Competencia> competencias = []
            while (resultado.next()) {
                Competencia c = new Competencia(resultado.getString('nome'))
                c.id = resultado.getInt('id')
                competencias << c
            }
            return competencias
        } finally {
            conexao.close()
        }
    }
}