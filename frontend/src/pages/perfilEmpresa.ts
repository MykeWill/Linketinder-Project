import { Chart } from 'chart.js/auto'
import * as candidatoService from '../service/candidatoService'
import * as recrutadorService from '../service/recrutadorService'
import * as vagaService from '../service/vagaService'
import type { RecrutadorInterface } from '../types/models'

const EMPRESA_LOGADA_ID = 1

export async function renderizarPerfilEmpresa() {
  const app = document.getElementById('app')!
  const candidatos = candidatoService.listarTodosCandidatosService()
  const competenciaContagem = candidatoService.contarCandidatosPorCompetenciaService()
  const vagasDaEmpresa = vagaService.listarVagasPorEmpresaService(EMPRESA_LOGADA_ID)
  let recrutadores: RecrutadorInterface[] = []
  let erroListagemRecrutadores = ''

  try {
    recrutadores = await recrutadorService.listarPorEmpresa(EMPRESA_LOGADA_ID)
  } catch (erro) {
    erroListagemRecrutadores = (erro as Error).message
  }

  const linhasTabelaCandidatos = candidatos.map(candidato => `
        <tr title="${escaparHtml(candidato.descricao)}">
            <td>${escaparHtml(candidato.descricao)}</td>
            <td>${candidato.competencias.map(escaparHtml).join(', ')}</td>
        </tr>
    `).join('')

  const linhasVagas = vagasDaEmpresa.map(vaga => `
        <tr title="${vaga.descricao}">
            <td>${vaga.titulo}</td>
            <td>${vaga.competencias.join(', ')}</td>
            <td><button class="btn-remover-vaga" data-id="${vaga.id}">Remover</button></td>
        </tr>
    `).join('')

  const linhasRecrutadores = recrutadores.map(recrutador => `
        <tr>
            <td>${escaparHtml(recrutador.nome)}</td>
            <td>${escaparHtml(recrutador.areaRecrutamento)}</td>
            <td>${escaparHtml(recrutador.email)}</td>
            <td><button class="btn-remover-recrutador" data-id="${escaparHtml(String(recrutador.id))}">Remover</button></td>
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
                    <th>Ações</th>
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
        <h2>Meus Recrutadores</h2>
        <table>
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Área</th>
                    <th>E-mail</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                ${linhasRecrutadores}
            </tbody>
        </table>
        <button id="btn-novo-recrutador">+ Cadastrar novo recrutador</button>
        <form id="form-novo-recrutador" class="escondido">
            <input type="text" id="recrutador-nome" placeholder="Nome" required>
            <input type="text" id="recrutador-area" placeholder="Área de recrutamento" required>
            <textarea id="recrutador-descricao" placeholder="Descrição" required></textarea>
            <input type="email" id="recrutador-email" placeholder="E-mail" required>
            <input type="text" id="recrutador-cpf" placeholder="CPF" required>
            <input type="date" id="recrutador-data-nascimento" required>
            <input type="password" id="recrutador-senha" placeholder="Senha" required>
            <button type="submit">Salvar recrutador</button>
        </form>
        <p id="mensagem-erro" class="mensagem-erro"></p>
        <h2>Candidatos disponíveis</h2>
        <table>
            <thead>
                <tr>
                    <th>Descrição profissional</th>
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

  const mensagemErro = document.getElementById('mensagem-erro')!
  mensagemErro.textContent = erroListagemRecrutadores

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
      mensagemErro.textContent = (erro as Error).message
    }
  })

  const botaoNovoRecrutador = document.getElementById('btn-novo-recrutador')!
  const formularioRecrutador = document.getElementById('form-novo-recrutador')!

  botaoNovoRecrutador.addEventListener('click', () => {
    formularioRecrutador.classList.toggle('escondido')
  })

  formularioRecrutador.addEventListener('submit', async (evento) => {
    evento.preventDefault()
    try {
      await recrutadorService.cadastrarRecrutador({
        empresaId: EMPRESA_LOGADA_ID,
        nome: (document.getElementById('recrutador-nome') as HTMLInputElement).value,
        areaRecrutamento: (document.getElementById('recrutador-area') as HTMLInputElement).value,
        descricao: (document.getElementById('recrutador-descricao') as HTMLTextAreaElement).value,
        email: (document.getElementById('recrutador-email') as HTMLInputElement).value,
        cpf: (document.getElementById('recrutador-cpf') as HTMLInputElement).value,
        dataNascimento: (document.getElementById('recrutador-data-nascimento') as HTMLInputElement).value,
        senha: (document.getElementById('recrutador-senha') as HTMLInputElement).value
      })
      await renderizarPerfilEmpresa()
    } catch (erro) {
      mensagemErro.textContent = (erro as Error).message
    }
  })

  document.querySelectorAll('.btn-remover-vaga').forEach(botao => {
    botao.addEventListener('click', () => {
      const id = (botao as HTMLElement).dataset.id!
      vagaService.removerVagaService(id)
      renderizarPerfilEmpresa()
    })
  })

  document.querySelectorAll('.btn-remover-recrutador').forEach(botao => {
    botao.addEventListener('click', async () => {
      const id = (botao as HTMLElement).dataset.id!
      try {
        await recrutadorService.removerRecrutador(id)
        await renderizarPerfilEmpresa()
      } catch (erro) {
        mensagemErro.textContent = (erro as Error).message
      }
    })
  })
}

function escaparHtml(valor: string): string {
  return valor.replace(/[&<>"']/g, caractere => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  })[caractere]!)
}