export function renderizarCadastro() {
  const app = document.getElementById('app')!
  app.innerHTML = `
        <main class="tela-autenticacao">
          <h1>Linketinder</h1>
          <p>Encontre conexões profissionais que combinam com você.</p>
          <h2>Como você quer se cadastrar?</h2>
          <div class="botoes-escolha">
            <a class="botao-escolha" href="#/cadastro-candidato">Sou candidato</a>
            <a class="botao-escolha" href="#/cadastro-empresa">Sou empresa</a>
            <a class="botao-escolha" href="#/login">Sou recrutador</a>
          </div>
          <p class="link-secundario">Já tem cadastro? <a href="#/login">Entrar</a></p>
        </main>
    `
}