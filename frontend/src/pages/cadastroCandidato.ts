import * as candidatoService from '../service/candidatoService'

export function renderizarCadastroCandidato() {
  const app = document.getElementById('app')!

  app.innerHTML = `
    <h1>Cadastro de Candidato</h1>
    <form id="form-cadastro-candidato">
      <input type="text" id="nome" placeholder="Nome" required>
      <input type="email" id="email" placeholder="E-mail" required>
      <input type="text" id="cpf" placeholder="CPF" required>
      <input type="number" id="idade" placeholder="Idade" required>
      <input type="text" id="estado" placeholder="Estado" required>
      <input type="text" id="cep" placeholder="CEP" required>
      <textarea id="descricao" placeholder="Descrição pessoal" required></textarea>
      <input type="text" id="formacao" placeholder="Formação" required>
      <input type="text" id="competencias" placeholder="Competências (separadas por vírgula)" required>
      <button type="submit">Cadastrar</button>
    </form>
    <p id="mensagem-erro"></p>
    <a href="#/">Voltar</a>
  `

  const formulario = document.getElementById('form-cadastro-candidato') as HTMLFormElement

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault()

    const nome = (document.getElementById('nome') as HTMLInputElement).value
    const email = (document.getElementById('email') as HTMLInputElement).value
    const cpf = (document.getElementById('cpf') as HTMLInputElement).value
    const idade = Number((document.getElementById('idade') as HTMLInputElement).value)
    const estado = (document.getElementById('estado') as HTMLInputElement).value
    const cep = (document.getElementById('cep') as HTMLInputElement).value
    const descricao = (document.getElementById('descricao') as HTMLTextAreaElement).value
    const formacao = (document.getElementById('formacao') as HTMLInputElement).value
    const competencias = (document.getElementById('competencias') as HTMLInputElement).value
        .split(',')
        .map(c => c.trim())
        .filter(c => c.length > 0)

    try {
      candidatoService.cadastrarCandidatoService({ nome, email, cpf, idade, estado, cep, descricao, formacao, competencias })
      window.location.hash = '#/perfil-candidato'
    } catch (erro) {
      document.getElementById('mensagem-erro')!.textContent = (erro as Error).message
    }
  })
}