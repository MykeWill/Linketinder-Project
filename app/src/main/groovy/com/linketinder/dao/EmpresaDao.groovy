package com.linketinder.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class EmpresaDao {

    Integer inserirEmpresa(String nome, String cnpj, String email, String descricao, String pais, String cep, String senha) {
        String sql = '''
            INSERT INTO empresa (nome, cnpj, email, descricao, pais, cep, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        '''
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, nome)
            stmt.setString(2, cnpj)
            stmt.setString(3, email)
            stmt.setString(4, descricao)
            stmt.setString(5, pais)
            stmt.setString(6, cep)
            stmt.setString(7, senha)
            ResultSet resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getInt('id')
        } finally {
            conexao.close()
        }
    }

    List<Map> listarTodasEmpresas() {
        String sql = 'SELECT id, nome, cnpj, email, descricao, pais, cep FROM empresa'
        Connection conexao = ConexaoDB.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet resultado = stmt.executeQuery()
            List<Map> empresas = []
            while (resultado.next()) {
                empresas << [
                        id       : resultado.getInt('id'),
                        nome     : resultado.getString('nome'),
                        cnpj     : resultado.getString('cnpj'),
                        email    : resultado.getString('email'),
                        descricao: resultado.getString('descricao'),
                        pais     : resultado.getString('pais'),
                        cep      : resultado.getString('cep')
                ]
            }
            return empresas
        } finally {
            conexao.close()
        }
    }

    void atualizarEmpresa(Integer id, String descricao) {
        String sql = 'UPDATE empresa SET descricao = ? WHERE id = ?'
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

    void removerEmpresa(Integer id) {
        String sql = 'DELETE FROM empresa WHERE id = ?'
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