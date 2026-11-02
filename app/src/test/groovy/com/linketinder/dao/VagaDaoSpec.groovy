package com.linketinder.dao

import java.sql.Array
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import spock.lang.Specification

class VagaDaoSpec extends Specification {

    def "listagem do recrutador filtra pela empresa vinculada"() {
        given:
        GroovyMock(ConexaoDB, global: true)
        def conexao = Mock(Connection)
        def statement = Mock(PreparedStatement)
        def resultado = Mock(ResultSet)
        ConexaoDB.executar(_ as Closure) >> { Closure consulta -> consulta.call(conexao) }
        conexao.prepareStatement(_ as String) >> statement
        statement.executeQuery() >> resultado
        resultado.next() >> false
        def dao = new VagaDao()

        when:
        def vagas = dao.listarVagasVisiveisParaRecrutador(1)

        then:
        1 * conexao.prepareStatement({ String sql ->
            sql.contains('WHERE v.empresa_id = ?') &&
                    !sql.toLowerCase().contains(' or ')
        }) >> statement
        1 * statement.setInt(1, 1)
        vagas.empty
    }

    def "listagem da empresa inclui vagas com recrutador nulo e preenchido"() {
        given:
        GroovyMock(ConexaoDB, global: true)
        def conexao = Mock(Connection)
        def statement = Mock(PreparedStatement)
        def resultado = Mock(ResultSet)
        def competencias = Mock(Array)
        ConexaoDB.executar(_ as Closure) >> { Closure consulta -> consulta.call(conexao) }
        conexao.prepareStatement(_ as String) >> statement
        statement.executeQuery() >> resultado
        competencias.getArray() >> (['Java'] as Object[])
        resultado.next() >>> [true, true, false]
        resultado.getInt('id') >>> [31, 32]
        resultado.getInt('empresa_id') >>> [7, 7]
        resultado.getString('nome') >>> ['Criada pela empresa', 'Criada pelo recrutador']
        resultado.getString('descricao') >>> ['Descrição fictícia', 'Outra descrição']
        resultado.getString('local') >>> ['Remoto', 'Remoto']
        resultado.getArray('competencias') >>> [competencias, competencias]
        resultado.getObject('recrutador_id') >>> [null, 1]
        def dao = new VagaDao()

        when:
        def vagas = dao.listarVagasPorEmpresa(7)

        then:
        1 * conexao.prepareStatement({ String sql ->
            sql.contains('WHERE v.empresa_id = ?') &&
                    !sql.toLowerCase().contains('join recrutador')
        }) >> statement
        1 * statement.setInt(1, 7)
        vagas*.id == [31, 32]
        vagas*.empresaId == [7, 7]
        vagas*.recrutadorId == [null, 1]
    }
}
