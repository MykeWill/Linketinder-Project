import * as vagaService from '../service/vagaService'

export function renderizarPerfilCandidato() {
  const app = document.getElementById('app')!
  const vagas = vagaService.listarTodasVagasService()
  const linhasTabela = vagas.map(vaga => `
    <tr title="${vaga.descricao}">
      <td>${vaga.titulo}</td>
      <td>${vaga.competencias.join(', ')}</td>
    </tr>
  `).join('')
  app.innerHTML = `
    <h1>Perfil do Candidato</h1>
    <p>Vagas disponíveis</p>
    <table>
      <thead>
        <tr>
          <th>Título da vaga</th>
          <th>Competências buscadas</th>
        </tr>
      </thead>
      <tbody>
        ${linhasTabela}
      </tbody>
    </table>
    <a href="#/">Voltar</a>
  `
}