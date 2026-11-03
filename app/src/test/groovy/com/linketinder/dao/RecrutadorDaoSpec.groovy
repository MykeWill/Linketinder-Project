package com.linketinder.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import spock.lang.Specification

class RecrutadorDaoSpec extends Specification {

    def "verifica se o candidato curtiu a vaga especifica"() {
        given:
        GroovyMock(ConexaoDB, global: true)
        def conexao = Mock(Connection)
        def statement = Mock(PreparedStatement)
        def resultado = Mock(ResultSet)
        ConexaoDB.executar(_ as Closure) >> { Closure consulta -> consulta.call(conexao) }
        conexao.prepareStatement(_ as String) >> statement
        statement.executeQuery() >> resultado
        resultado.next() >> true
        resultado.getBoolean(1) >> true
        def dao = new RecrutadorDao()

        when:
        boolean match = dao.candidatoCurtiuVaga(7, 42)

        then:
        1 * conexao.prepareStatement({ String sql ->
            sql.contains('FROM curtida_candidato_vaga ccv') &&
                    sql.contains('ccv.candidato_id = ?') &&
                    sql.contains('ccv.vaga_id = ?')
        }) >> statement
        1 * statement.setInt(1, 7)
        1 * statement.setInt(2, 42)
        match
    }

    def "insere curtida da empresa com o vaga_id selecionado"() {
        given:
        GroovyMock(ConexaoDB, global: true)
        def conexao = Mock(Connection)
        def statement = Mock(PreparedStatement)
        ConexaoDB.executar(_ as Closure) >> { Closure consulta -> consulta.call(conexao) }
        conexao.prepareStatement(_ as String) >> statement
        def dao = new RecrutadorDao()

        when:
        dao.curtirCandidato(1, 7, 23, 42)

        then:
        1 * conexao.prepareStatement({ String sql ->
            sql.contains('empresa_id, candidato_id, recrutador_id, vaga_id') &&
                    sql.contains('VALUES (?, ?, ?, ?)')
        }) >> statement
        1 * statement.setInt(1, 1)
        1 * statement.setInt(2, 7)
        1 * statement.setInt(3, 23)
        1 * statement.setInt(4, 42)
        1 * statement.executeUpdate()
    }
}
