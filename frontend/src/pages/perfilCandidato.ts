import * as empresaService from '../service/empresaService'

export function renderizarPerfilCandidato() {
  const app = document.getElementById('app')!
  const vagas = empresaService.listarTodasEmpresasService()

  const linhasTabela = vagas.map(vaga => `
    <tr title="${vaga.descricao}">
      <td>${vaga.estado}</td>
      <td>${vaga.pais}</td>
      <td>${vaga.competencias.join(', ')}</td>
    </tr>
  `).join('')

  app.innerHTML = `
    <h1>Perfil do Candidato</h1>
    <p>Vagas disponíveis (empresas em anonimato até o match)</p>
    <table>
      <thead>
        <tr>
          <th>Estado</th>
          <th>País</th>
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