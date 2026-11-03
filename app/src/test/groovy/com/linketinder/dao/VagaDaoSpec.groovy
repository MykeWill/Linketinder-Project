package com.linketinder.dao

import java.sql.Array
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.math.BigDecimal
import spock.lang.Specification

class VagaDaoSpec extends Specification {

    def "listagem de vagas nao curtidas filtra pelo candidato informado"() {
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
        def vagas = dao.listarVagasNaoCurtidasPorCandidato(42)

        then:
        1 * conexao.prepareStatement({ String sql ->
            sql.contains('NOT EXISTS') &&
                    sql.contains('ccv.candidato_id = ?') &&
                    sql.contains('ccv.vaga_id = v.id')
        }) >> statement
        1 * statement.setInt(1, 42)
        1 * statement.setInt(2, 42)
        vagas.empty
    }

    def "listagem calcula afinidade pelas competencias do candidato"() {
        given:
        GroovyMock(ConexaoDB, global: true)
        def conexao = Mock(Connection)
        def statement = Mock(PreparedStatement)
        def resultado = Mock(ResultSet)
        def competencias = Mock(Array)
        ConexaoDB.executar(_ as Closure) >> { Closure consulta -> consulta.call(conexao) }
        conexao.prepareStatement(_ as String) >> statement
        statement.executeQuery() >> resultado
        competencias.getArray() >> (['Java', 'Groovy', 'SQL', 'Docker'] as Object[])
        resultado.next() >>> [true, false]
        resultado.getInt('empresa_id') >> 3
        resultado.getString('nome') >> 'Desenvolvedor'
        resultado.getString('descricao') >> 'Descrição fictícia'
        resultado.getString('local') >> 'Remoto'
        resultado.getArray('competencias') >> competencias
        resultado.getObject('recrutador_id') >> null
        resultado.getInt('id') >> 17
        resultado.getBigDecimal('afinidade') >> new BigDecimal('50.0')
        def dao = new VagaDao()

        when:
        def vagas = dao.listarVagasNaoCurtidasPorCandidato(42)

        then:
        1 * conexao.prepareStatement({ String sql ->
            sql.contains('COUNT(DISTINCT CASE') &&
                    sql.contains('COUNT(DISTINCT vc.competencia_id)') &&
                    sql.contains('100.0 / NULLIF') &&
                    sql.contains(') AS afinidade')
        }) >> statement
        1 * statement.setInt(1, 42)
        1 * statement.setInt(2, 42)
        vagas.size() == 1
        vagas[0].afinidade == new BigDecimal('50.0')
    }

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
