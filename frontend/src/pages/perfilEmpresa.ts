import { Chart } from 'chart.js/auto'
import * as candidatoService from '../service/candidatoService'
import * as vagaService from '../service/vagaService'

const EMPRESA_LOGADA_ID = '1'

export function renderizarPerfilEmpresa() {
  const app = document.getElementById('app')!
  const candidatos = candidatoService.listarTodosCandidatosService()
  const competenciaContagem = candidatoService.contarCandidatosPorCompetenciaService()
  const vagasDaEmpresa = vagaService.listarVagasPorEmpresaService(EMPRESA_LOGADA_ID)

  const linhasTabelaCandidatos = candidatos.map(candidato => `
        <tr title="${candidato.descricao}">
            <td>${candidato.formacao}</td>
            <td>${candidato.competencias.join(', ')}</td>
        </tr>
    `).join('')

  const linhasVagas = vagasDaEmpresa.map(vaga => `
        <tr title="${vaga.descricao}">
            <td>${vaga.titulo}</td>
            <td>${vaga.competencias.join(', ')}</td>
        </tr>
    `).join('')

  app.innerHTML = `
        <h1>Perfil da Empresa</h1>
        <h2>Minhas vagas</h2>
        <table>
            <thead>
                <tr>
                    <th>Título</th>
                    <th>Competências</th>
                </tr>
            </thead>
            <tbody>
                ${linhasVagas}
            </tbody>
        </table>
        <button id="btn-nova-vaga">+ Cadastrar nova vaga</button>
        <form id="form-nova-vaga" class="escondido">
            <input type="text" id="titulo" placeholder="Título da vaga" required>
            <textarea id="descricao" placeholder="Descrição da vaga" required></textarea>
            <input type="text" id="competencias" placeholder="Competências exigidas (separadas por vírgula)" required>
            <button type="submit">Salvar vaga</button>
        </form>
        <p id="mensagem-erro"></p>
        <h2>Candidatos disponíveis</h2>
        <table>
            <thead>
                <tr>
                    <th>Formação</th>
                    <th>Competências</th>
                </tr>
            </thead>
            <tbody>
                ${linhasTabelaCandidatos}
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

  const botaoNovaVaga = document.getElementById('btn-nova-vaga')!
  const formularioVaga = document.getElementById('form-nova-vaga')!

  botaoNovaVaga.addEventListener('click', () => {
    formularioVaga.classList.toggle('escondido')
  })

  formularioVaga.addEventListener('submit', (evento) => {
    evento.preventDefault()

    const titulo = (document.getElementById('titulo') as HTMLInputElement).value
    const descricao = (document.getElementById('descricao') as HTMLTextAreaElement).value
    const competencias = (document.getElementById('competencias') as HTMLInputElement).value
        .split(',')
        .map(c => c.trim())
        .filter(c => c.length > 0)

    try {
      vagaService.cadastrarVagaService({ empresaId: EMPRESA_LOGADA_ID, titulo, descricao, competencias })
      renderizarPerfilEmpresa()
    } catch (erro) {
      document.getElementById('mensagem-erro')!.textContent = (erro as Error).message
    }
  })
}