import { Chart } from 'chart.js/auto'
import * as candidatoService from '../service/candidatoService'

export function renderizarPerfilEmpresa() {
  const app = document.getElementById('app')!
  const candidatos = candidatoService.listarTodosCandidatosService()
  const competenciaContagem = candidatoService.contarCandidatosPorCompetenciaService()

  const linhasTabela = candidatos.map(candidato => `
        <tr title="${candidato.descricao}">
            <td>${candidato.formacao}</td>
            <td>${candidato.competencias.join(', ')}</td>
        </tr>
    `).join('')

  app.innerHTML = `
        <h1>Perfil da Empresa</h1>
        <p>Candidatos disponíveis</p>
        <table>
            <thead>
                <tr>
                    <th>Formação</th>
                    <th>Competências</th>
                </tr>
            </thead>
            <tbody>
                ${linhasTabela}
            </tbody>
        </table>
        <h2>Candidatos por competência</h2>
        <canvas id="grafico-competencias"></canvas>
        <a href="#/">Voltar</a>
    `

  const canvas = document.getElementById('grafico-competencias') as HTMLCanvasElement

  new Chart(canvas, {
    type: 'bar',
    data: {
      labels: Object.keys(competenciaContagem),
      datasets: [{
        label: 'Candidatos',
        data: Object.values(competenciaContagem)
      }]
    }
  })
}