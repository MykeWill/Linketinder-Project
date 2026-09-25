package com.linketinder.dao

import com.linketinder.model.Candidato
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CandidatoDao {

    Integer inserirCandidato(Candidato c) {
        String sql = '''
            INSERT INTO candidato (nome, email, cpf, idade, estado, cep, descricao, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        '''
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, c.nome)
            stmt.setString(2, c.email)
            stmt.setString(3, c.cpf)
            stmt.setInt(4, c.idade)
            stmt.setString(5, c.estado)
            stmt.setString(6, c.cep)
            stmt.setString(7, c.descricao)
            stmt.setString(8, c.senha)
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

    List<Map> listarCandidatosAnonimos() {
        String sql = '''
            SELECT c.id, c.descricao, array_agg(comp.nome) AS competencias
            FROM candidato c
            JOIN candidato_competencia cc ON c.id = cc.candidato_id
            JOIN competencia comp ON cc.competencia_id = comp.id
            GROUP BY c.id
            ORDER BY c.id
        '''
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet resultado = stmt.executeQuery()
            List<Map> candidatos = []
            while (resultado.next()) {
                candidatos << [
                        id          : resultado.getInt('id'),
                        descricao   : resultado.getString('descricao'),
                        competencias: (resultado.getArray('competencias').array as List).toList()
                ]
            }
            return candidatos
        } finally {
            conexao.close()
        }
    }

    List<Candidato> listarTodosCandidatos() {
        String sql = '''
            SELECT c.id, c.nome, c.email, c.cpf, c.idade, c.estado, c.cep, c.descricao, c.senha,
                   array_agg(comp.nome) AS competencias
            FROM candidato c
            LEFT JOIN candidato_competencia cc ON c.id = cc.candidato_id
            LEFT JOIN competencia comp ON cc.competencia_id = comp.id
            GROUP BY c.id
            ORDER BY c.id
        '''
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet resultado = stmt.executeQuery()
            List<Candidato> candidatos = []
            while (resultado.next()) {
                Candidato c = new Candidato(
                        resultado.getString('nome'),
                        resultado.getString('email'),
                        resultado.getString('cpf'),
                        resultado.getInt('idade'),
                        resultado.getString('estado'),
                        resultado.getString('cep'),
                        resultado.getString('descricao'),
                        resultado.getString('senha'),
                        (resultado.getArray('competencias')?.array as List)?.toList() ?: []
                )
                c.id = resultado.getInt('id')
                candidatos << c
            }
            return candidatos
        } finally {
            conexao.close()
        }
    }

    void atualizarCandidato(Candidato c) {
        String sql = 'UPDATE candidato SET nome = ?, email = ?, cpf = ?, idade = ?, estado = ?, cep = ?, descricao = ?, senha = ? WHERE id = ?'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, c.nome)
            stmt.setString(2, c.email)
            stmt.setString(3, c.cpf)
            stmt.setInt(4, c.idade)
            stmt.setString(5, c.estado)
            stmt.setString(6, c.cep)
            stmt.setString(7, c.descricao)
            stmt.setString(8, c.senha)
            stmt.setInt(9, c.id)
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