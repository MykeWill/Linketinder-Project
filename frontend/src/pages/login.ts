import * as candidatoService from '../service/candidatoService'
import * as empresaService from '../service/empresaService'

export function renderizarLogin() {
  const app = document.getElementById('app')!

  app.innerHTML = `
        <h1>Login</h1>
        <form id="form-login">
            <input type="email" id="email" placeholder="E-mail" required>
            <input type="password" id="senha" placeholder="Senha" required>
            <button type="submit">Entrar</button>
        </form>
        <p id="mensagem-erro"></p>
        <a href="#/">Ainda não tenho cadastro</a>
    `

  const formulario = document.getElementById('form-login') as HTMLFormElement

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault()
    const email = (document.getElementById('email') as HTMLInputElement).value

    const candidato = candidatoService.buscarCandidatoPorEmailService(email)
    if (candidato) {
      window.location.hash = '#/perfil-candidato'
      return
    }

    const empresa = empresaService.buscarEmpresaPorEmailService(email)
    if (empresa) {
      window.location.hash = '#/perfil-empresa'
      return
    }

    document.getElementById('mensagem-erro')!.textContent = 'E-mail não cadastrado.'
  })
}