type RotaHandler = () => void

const rotas: Record<string, RotaHandler> = {}

export function registrarRota(caminho: string, handler: RotaHandler) {
    rotas[caminho] = handler
}

function renderizarRotaAtual() {
    const caminho = window.location.hash.slice(1) || '/'
    const handler = rotas[caminho]

    if (handler) {
        handler()
    } else {
        const app = document.getElementById('app')!
        app.innerHTML = '<h1>404 - Página não encontrada</h1>'
    }
}

export function iniciarRoteador() {
    window.addEventListener('hashchange', renderizarRotaAtual)
    window.addEventListener('DOMContentLoaded', renderizarRotaAtual)
}