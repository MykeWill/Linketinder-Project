package com.linketinder.server

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import spock.lang.Specification

class CorsFilterSpec extends Specification {

    def "preflight OPTIONS retorna 204 com os cabecalhos CORS sem executar o handler"() {
        given:
        def filter = new CorsFilter()
        def exchange = Mock(HttpExchange)
        boolean handlerExecutado = false
        def chain = new Filter.Chain([], { HttpExchange ignored -> handlerExecutado = true } as HttpHandler)
        def headers = new Headers()
        exchange.requestMethod >> "OPTIONS"
        exchange.responseHeaders >> headers

        when:
        filter.doFilter(exchange, chain)

        then:
        headers.getFirst("Access-Control-Allow-Origin") == "*"
        headers.getFirst("Access-Control-Allow-Methods") == "GET, POST, PUT, DELETE, OPTIONS"
        headers.getFirst("Access-Control-Allow-Headers") == "Content-Type, Authorization"
        1 * exchange.sendResponseHeaders(204, -1)
        1 * exchange.close()
        !handlerExecutado
    }

    def "requisicoes normais passam pelo handler com os cabecalhos CORS"() {
        given:
        def filter = new CorsFilter()
        def exchange = Mock(HttpExchange)
        boolean handlerExecutado = false
        def chain = new Filter.Chain([], { HttpExchange ignored -> handlerExecutado = true } as HttpHandler)
        def headers = new Headers()
        exchange.requestMethod >> "GET"
        exchange.responseHeaders >> headers

        when:
        filter.doFilter(exchange, chain)

        then:
        headers.getFirst("Access-Control-Allow-Origin") == "*"
        headers.getFirst("Access-Control-Allow-Methods") == "GET, POST, PUT, DELETE, OPTIONS"
        headers.getFirst("Access-Control-Allow-Headers") == "Content-Type, Authorization"
        handlerExecutado
        0 * exchange.sendResponseHeaders(_, _)
    }
}
