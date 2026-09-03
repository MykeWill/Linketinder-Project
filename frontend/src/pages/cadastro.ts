export function renderizarCadastro() {
  const app = document.getElementById('app')!
  app.innerHTML = `
        <h1>Linketinder</h1>
        <p>Como você quer se cadastrar?</p>
        <a href="#/cadastro-candidato">Sou candidato</a>
        <a href="#/cadastro-empresa">Sou empresa</a>
        <p><a href="#/login">Já tenho cadastro</a></p>
    `
}