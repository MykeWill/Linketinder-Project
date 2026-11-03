package com.linketinder.server

import com.sun.net.httpserver.HttpExchange

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class SessaoHttp {

    private static final long DURACAO_SEGUNDOS = 8 * 60 * 60
    private static final Map<String, Map> SESSOES = new ConcurrentHashMap<>()

    private SessaoHttp() {}

    static Map criar(Map principal) {
        Instant agora = Instant.now()
        SESSOES.entrySet().removeIf { entrada ->
            agora.isAfter(entrada.value.expiraEm as Instant)
        }
        String token = UUID.randomUUID().toString()
        Map sessao = [
                id: principal.id,
                nome: principal.nome,
                tipo: principal.tipo,
                token: token
        ]
        if (principal.empresaId != null) {
            sessao.empresaId = principal.empresaId
        }
        SESSOES[token] = [
                sessao: sessao,
                expiraEm: agora.plusSeconds(DURACAO_SEGUNDOS)
        ]
        return sessao
    }

    static Map obter(HttpExchange exchange) {
        String autorizacao = exchange.requestHeaders.getFirst('Authorization')
        if (!autorizacao?.startsWith('Bearer ')) {
            return null
        }

        String token = autorizacao.substring('Bearer '.length()).trim()
        Map registro = SESSOES[token]
        if (registro == null) {
            return null
        }
        if (Instant.now().isAfter(registro.expiraEm as Instant)) {
            SESSOES.remove(token)
            return null
        }
        return registro.sessao as Map
    }
}
