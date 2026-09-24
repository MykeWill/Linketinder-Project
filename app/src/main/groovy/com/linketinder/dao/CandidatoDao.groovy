package com.linketinder.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CandidatoDao {

    Integer inserirCandidato(String nome, String sobrenome, String dataNasc, String email, String cpf,
                             String pais, String cep, String descricao, String senha) {
        String sql = '''
            INSERT INTO candidato (nome, sobrenome, data_nasc, email, cpf, pais, cep, descricao, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        '''
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, nome)
            stmt.setString(2, sobrenome)
            stmt.setString(3, dataNasc)
            stmt.setString(4, email)
            stmt.setString(5, cpf)
            stmt.setString(6, pais)
            stmt.setString(7, cep)
            stmt.setString(8, descricao)
            stmt.setString(9, senha)
            ResultSet resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getInt('id')
        } finally {
            conexao.close()
        }
    }

    void vincularCompetenciaAoCandidato(Integer candidatoId, Integer competenciaId) {
        String sql = 'INSERT INTO candidato_competencia (candidato_id, competencia_id) VALUES (?, ?)'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    List<Map> listarTodosCandidatos() {
        String sql = 'SELECT id, nome, sobrenome, data_nasc, email, cpf, pais, cep, descricao FROM candidato'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet resultado = stmt.executeQuery()
            List<Map> candidatos = []
            while (resultado.next()) {
                candidatos << [
                        id       : resultado.getInt('id'),
                        nome     : resultado.getString('nome'),
                        sobrenome: resultado.getString('sobrenome'),
                        dataNasc : resultado.getDate('data_nasc'),
                        email    : resultado.getString('email'),
                        cpf      : resultado.getString('cpf'),
                        pais     : resultado.getString('pais'),
                        cep      : resultado.getString('cep'),
                        descricao: resultado.getString('descricao')
                ]
            }
            return candidatos
        } finally {
            conexao.close()
        }
    }

    void atualizarCandidato(Integer id, String descricao) {
        String sql = 'UPDATE candidato SET descricao = ? WHERE id = ?'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, descricao)
            stmt.setInt(2, id)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    void removerCandidato(Integer id) {
        String sql = 'DELETE FROM candidato WHERE id = ?'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }
}