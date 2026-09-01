export function renderizarLogin() {
  const app = document.getElementById('app')!

  app.innerHTML = `
    <h1>Login</h1>
    <form id="form-login">
      <input type="email" id="email" placeholder="E-mail" required>
      <input type="password" id="senha" placeholder="Senha" required>
      <select id="tipo-usuario" required>
        <option value="">Sou...</option>
        <option value="candidato">Candidato</option>
        <option value="empresa">Empresa</option>
      </select>
      <button type="submit">Entrar</button>
    </form>
    <a href="#/">Ainda não tenho cadastro</a>
  `

  const formulario = document.getElementById('form-login') as HTMLFormElement

  formulario.addEventListener('submit', (evento) => {
    evento.preventDefault()
    const tipoUsuario = (document.getElementById('tipo-usuario') as HTMLSelectElement).value

    if (tipoUsuario === 'candidato') {
      window.location.hash = '#/perfil-candidato'
    } else if (tipoUsuario === 'empresa') {
      window.location.hash = '#/perfil-empresa'
    }
  })
}