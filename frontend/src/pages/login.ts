import { request } from '../service/api'
import { salvarSessao, validarSessao } from '../service/sessaoService'

export function renderizarLogin() {
  const app = document.getElementById('app')!

  app.innerHTML = `
    <h1>Login</h1>
    <form id="form-login">
      <label for="tipo">Tipo de usuário</label>
      <select id="tipo" required>
        <option value="">Selecione</option>
        <option value="CANDIDATO">Candidato</option>
        <option value="EMPRESA">Empresa</option>
        <option value="RECRUTADOR">Recrutador</option>
      </select>
      <input type="email" id="email" placeholder="E-mail" required>
      <input type="password" id="senha" placeholder="Senha" required>
      <button type="submit">Entrar</button>
    </form>
    <p id="mensagem-erro" class="mensagem-erro" role="alert"></p>
    <a href="#/">Ainda não tenho cadastro</a>
  `

  const formulario = document.getElementById('form-login') as HTMLFormElement
  const botao = formulario.querySelector<HTMLButtonElement>('button[type="submit"]')!

  formulario.addEventListener('submit', async evento => {
    evento.preventDefault()
    const mensagemErro = document.getElementById('mensagem-erro')!
    mensagemErro.textContent = ''
    botao.disabled = true

    try {
      const resposta = await request<unknown>('/login', {
        method: 'POST',
        body: JSON.stringify({
          email: (document.getElementById('email') as HTMLInputElement).value,
          senha: (document.getElementById('senha') as HTMLInputElement).value,
          tipo: (document.getElementById('tipo') as HTMLSelectElement).value
        })
      })
      const sessao = validarSessao(resposta)
      if (!sessao) {
        throw new Error('Resposta inválida do servidor ao autenticar.')
      }

      salvarSessao(sessao)
      window.location.hash = {
        CANDIDATO: '#/perfil-candidato',
        EMPRESA: '#/perfil-empresa',
        RECRUTADOR: '#/perfil-recrutador'
      }[sessao.tipo]
    } catch (erro) {
      mensagemErro.textContent =
        erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
    } finally {
      botao.disabled = false
    }
  })
}
