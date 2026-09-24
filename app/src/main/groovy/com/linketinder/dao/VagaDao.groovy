package com.linketinder.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class VagaDao {

    Integer inserirVaga(Integer empresaId, String nome, String descricao, String local) {
        String sql = '''
            INSERT INTO vaga (empresa_id, nome, descricao, local)
            VALUES (?, ?, ?, ?)
            RETURNING id
        '''
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, empresaId)
            stmt.setString(2, nome)
            stmt.setString(3, descricao)
            stmt.setString(4, local)
            ResultSet resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getInt('id')
        } finally {
            conexao.close()
        }
    }

    void vincularCompetenciaAVaga(Integer vagaId, Integer competenciaId) {
        String sql = 'INSERT INTO vaga_competencia (vaga_id, competencia_id) VALUES (?, ?)'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    List<Map> listarTodasVagas() {
        String sql = 'SELECT id, empresa_id, nome, descricao, local FROM vaga'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet resultado = stmt.executeQuery()
            List<Map> vagas = []
            while (resultado.next()) {
                vagas << [
                        id       : resultado.getInt('id'),
                        empresaId: resultado.getInt('empresa_id'),
                        nome     : resultado.getString('nome'),
                        descricao: resultado.getString('descricao'),
                        local    : resultado.getString('local')
                ]
            }
            return vagas
        } finally {
            conexao.close()
        }
    }

    List<Map> listarVagasPorEmpresa(Integer empresaId) {
        String sql = 'SELECT id, empresa_id, nome, descricao, local FROM vaga WHERE empresa_id = ?'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, empresaId)
            ResultSet resultado = stmt.executeQuery()
            List<Map> vagas = []
            while (resultado.next()) {
                vagas << [
                        id       : resultado.getInt('id'),
                        empresaId: resultado.getInt('empresa_id'),
                        nome     : resultado.getString('nome'),
                        descricao: resultado.getString('descricao'),
                        local    : resultado.getString('local')
                ]
            }
            return vagas
        } finally {
            conexao.close()
        }
    }

    void atualizarVaga(Integer id, String descricao) {
        String sql = 'UPDATE vaga SET descricao = ? WHERE id = ?'
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

    void removerVaga(Integer id) {
        String sql = 'DELETE FROM vaga WHERE id = ?'
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