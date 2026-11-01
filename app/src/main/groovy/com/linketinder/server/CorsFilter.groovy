package com.linketinder.server

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.HttpExchange

class CorsFilter extends Filter {

    @Override
    String description() {
        "Adiciona os cabeçalhos CORS às respostas HTTP"
    }

    @Override
    void doFilter(HttpExchange exchange, Chain chain) {
        exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")
        exchange.responseHeaders.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        exchange.responseHeaders.set("Access-Control-Allow-Headers", "Content-Type, Authorization")

        if (exchange.requestMethod == "OPTIONS") {
            exchange.sendResponseHeaders(204, -1)
            exchange.close()
            return
        }

        chain.doFilter(exchange)
    }
}
