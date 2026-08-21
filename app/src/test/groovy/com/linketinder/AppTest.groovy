package com.linketinder

import com.linketinder.model.Candidato
import spock.lang.Specification

class AppTest extends Specification {

    def "candidato deve armazenar seus dados corretamente"() {
        given:
        def candidato = new Candidato(
                "Myke",
                "myke@email.com",
                "12345678900",
                25,
                "PA",
                "66000-000",
                "Desenvolvedor Backend",
                ["Java", "Groovy"]
        )

        expect:
        candidato.nome == "Myke"
        candidato.email == "myke@email.com"
        candidato.cpf == "12345678900"
        candidato.idade == 25
    }
}
