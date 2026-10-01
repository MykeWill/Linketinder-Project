package com.linketinder.dao

import com.linketinder.model.Empresa

class EmpresaDao {

    Integer inserirEmpresa(Empresa e) {
        String sql = '''
            INSERT INTO empresa (nome, cnpj, email, descricao, pais, estado, cep, senha)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setString(1, e.nome)
            stmt.setString(2, e.cnpj)
            stmt.setString(3, e.email)
            stmt.setString(4, e.descricao)
            stmt.setString(5, e.pais)
            stmt.setString(6, e.estado)
            stmt.setString(7, e.cep)
            stmt.setString(8, e.senha)
            def resultado = stmt.executeQuery()
            resultado.next()
            return resultado.getInt('id')
        }
    }

    List<Empresa> listarTodasEmpresas() {
        String sql = 'SELECT id, nome, cnpj, email, descricao, pais, estado, cep, senha FROM empresa'
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            def resultado = stmt.executeQuery()
            List<Empresa> empresas = []
            while (resultado.next()) {
                Empresa e = new Empresa(
                        resultado.getString('nome'),
                        resultado.getString('email'),
                        resultado.getString('cnpj'),
                        resultado.getString('pais'),
                        resultado.getString('estado'),
                        resultado.getString('cep'),
                        resultado.getString('descricao'),
                        resultado.getString('senha')
                )
                e.id = resultado.getInt('id')
                empresas << e
            }
            return empresas
        }
    }

    List<Map> listarEmpresasAnonimas() {
        String sql = '''
            SELECT v.id AS vaga_id, v.nome AS vaga_nome, v.descricao, v.local,
                   array_agg(comp.nome) AS competencias_exigidas
            FROM vaga v
            LEFT JOIN vaga_competencia vc ON v.id = vc.vaga_id
            LEFT JOIN competencia comp ON vc.competencia_id = comp.id
            GROUP BY v.id
            ORDER BY v.id
        '''
        return ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            def resultado = stmt.executeQuery()
            List<Map> vagas = []
            while (resultado.next()) {
                vagas << [
                        id                  : resultado.getInt('vaga_id'),
                        nome                : resultado.getString('vaga_nome'),
                        descricao           : resultado.getString('descricao'),
                        local               : resultado.getString('local'),
                        competenciasExigidas: (resultado.getArray('competencias_exigidas')?.array as List)?.toList() ?: []
                ]
            }
            return vagas
        }
    }

    void atualizarEmpresa(Empresa e) {
        String sql = 'UPDATE empresa SET nome = ?, cnpj = ?, email = ?, descricao = ?, pais = ?, estado = ?, cep = ?, senha = ? WHERE id = ?'
        ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setString(1, e.nome)
            stmt.setString(2, e.cnpj)
            stmt.setString(3, e.email)
            stmt.setString(4, e.descricao)
            stmt.setString(5, e.pais)
            stmt.setString(6, e.estado)
            stmt.setString(7, e.cep)
            stmt.setString(8, e.senha)
            stmt.setInt(9, e.id)
            stmt.executeUpdate()
        }
    }

    void removerEmpresa(Integer id) {
        String sql = 'DELETE FROM empresa WHERE id = ?'
        ConexaoDB.executar { conexao ->
            def stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            stmt.executeUpdate()
        }
    }
}