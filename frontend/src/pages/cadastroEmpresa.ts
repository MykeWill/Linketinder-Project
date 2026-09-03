import * as empresaService from '../service/empresaService'

export function renderizarCadastroEmpresa() {
  const app = document.getElementById('app')!
  app.innerHTML = `
        <h1>Cadastro de Empresa</h1>
        <form id="form-cadastro-empresa">
            <input type="text" id="nome" placeholder="Nome" required>
            <input type="email" id="emailCorporativo" placeholder="E-mail corporativo" required>
            <input type="text" id="cnpj" placeholder="CNPJ" required>
            <input type="text" id="pais" placeholder="País" required>
            <input type="text" id="estado" placeholder="Estado" required>
            <input type="text" id="cep" placeholder="CEP" required>
            <textarea id="descricao" placeholder="Descrição da empresa" required></textarea>
            <button type="submit">Cadastrar</button>
        </form>
        <p id="mensagem-erro"></p>
        <a href="#/">Voltar</a>
    `
  const formulario = document.getElementById('form-cadastro-empresa') as HTMLFormElement
  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault()
    const nome = (document.getElementById('nome') as HTMLInputElement).value
    const emailCorporativo = (document.getElementById('emailCorporativo') as HTMLInputElement).value
    const cnpj = (document.getElementById('cnpj') as HTMLInputElement).value
    const pais = (document.getElementById('pais') as HTMLInputElement).value
    const estado = (document.getElementById('estado') as HTMLInputElement).value
    const cep = (document.getElementById('cep') as HTMLInputElement).value
    const descricao = (document.getElementById('descricao') as HTMLTextAreaElement).value
    try {
      empresaService.cadastrarEmpresaService({ nome, emailCorporativo, cnpj, pais, estado, cep, descricao })
      window.location.hash = '#/perfil-empresa'
    } catch (erro) {
      document.getElementById('mensagem-erro')!.textContent = (erro as Error).message
    }
  })
}