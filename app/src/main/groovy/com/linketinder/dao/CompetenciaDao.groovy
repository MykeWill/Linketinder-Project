package com.linketinder.dao

import com.linketinder.model.Competencia

class CompetenciaDao {

    Integer buscarIdPorNome(String nome) {
        String sql = 'SELECT id FROM competencia WHERE nome = ?'
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setString(1, nome)
            def resultado = stmt.executeQuery()
            if (resultado.next()) {
                return resultado.getInt('id')
            }
            return null
        }
    }

    Integer inserir(String nome) {
        String sql = 'INSERT INTO competencia (nome) VALUES (?) RETURNING id'
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setString(1, nome)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getInt('id')
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
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            def resultado = stmt.executeQuery()
            List<Competencia> competencias = []
            while (resultado.next()) {
                Competencia c = new Competencia(resultado.getString('nome'))
                c.id = resultado.getInt('id')
                competencias << c
            }
            return competencias
        }
    }
}