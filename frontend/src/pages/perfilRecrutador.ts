import * as recrutadorService from '../service/recrutadorService'
import * as vagaService from '../service/vagaService'
import type {
  CandidatoDisponivelInterface,
  MatchInterface,
  VagaInterface
} from '../types/models'

const RECRUTADOR_LOGADO_ID = 1
const EMPRESA_LOGADA_ID = 1

export async function renderizarPerfilRecrutador(): Promise<void> {
  const app = document.getElementById('app')!
  app.innerHTML = `
    <h1>Área do Recrutador</h1>
    <p id="mensagem-erro" class="mensagem-erro" role="alert"></p>
    <section>
      <h2>Resumo</h2>
      <p id="resumo-recrutador">Carregando dados da área...</p>
    </section>
    <section>
      <h2>Vagas</h2>
      <p id="erro-vagas" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr><th>Título</th><th>Descrição</th><th>Competências</th></tr>
        </thead>
        <tbody id="lista-vagas"></tbody>
      </table>
      <button id="btn-nova-vaga" type="button">+ Nova Vaga</button>
      <form id="form-nova-vaga" class="escondido">
        <input type="text" id="vaga-titulo" placeholder="Título da vaga" required>
        <textarea id="vaga-descricao" placeholder="Descrição da vaga" required></textarea>
        <input type="text" id="vaga-competencias"
          placeholder="Competências (separadas por vírgula)" required>
        <button type="submit">Salvar vaga</button>
      </form>
    </section>
    <section>
      <h2>Candidatos Disponíveis</h2>
      <p id="erro-candidatos" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr><th>Descrição profissional</th><th>Competências</th><th>Ações</th></tr>
        </thead>
        <tbody id="lista-candidatos"></tbody>
      </table>
    </section>
    <section>
      <h2>Matches</h2>
      <p id="erro-matches" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr>
            <th>Empresa</th><th>Recrutador</th><th>Candidato</th>
          </tr>
        </thead>
        <tbody id="lista-matches"></tbody>
      </table>
    </section>
    <a href="#/">Voltar</a>
  `

  const erroGeral = document.getElementById('mensagem-erro')!
  const vagasResultado = await carregar(
    () => vagaService.listarVagasVisiveisParaRecrutadorService(
      EMPRESA_LOGADA_ID,
      RECRUTADOR_LOGADO_ID
    ),
    'erro-vagas'
  )
  const candidatosResultado = await carregar(
    () => recrutadorService.listarCandidatos(RECRUTADOR_LOGADO_ID),
    'erro-candidatos'
  )
  const matchesResultado = await carregar(
    () => recrutadorService.listarMatches(RECRUTADOR_LOGADO_ID),
    'erro-matches'
  )

  document.querySelector('#resumo-recrutador')!.textContent =
    `${vagasResultado?.length ?? 0} vagas, ` +
    `${candidatosResultado?.length ?? 0} candidatos disponíveis e ` +
    `${matchesResultado?.length ?? 0} matches.`

  renderizarVagas(vagasResultado)
  renderizarCandidatos(candidatosResultado)
  renderizarMatches(matchesResultado)

  document.getElementById('btn-nova-vaga')!.addEventListener('click', () => {
    document.getElementById('form-nova-vaga')!.classList.toggle('escondido')
  })

  document.getElementById('form-nova-vaga')!.addEventListener('submit', async evento => {
    evento.preventDefault()
    const competencias = (document.getElementById('vaga-competencias') as HTMLInputElement)
      .value.split(',').map(competencia => competencia.trim()).filter(Boolean)

    try {
      await vagaService.cadastrarVagaPorRecrutadorService({
        empresaId: EMPRESA_LOGADA_ID,
        recrutadorId: RECRUTADOR_LOGADO_ID,
        nome: (document.getElementById('vaga-titulo') as HTMLInputElement).value,
        descricao: (document.getElementById('vaga-descricao') as HTMLTextAreaElement).value,
        competencias
      })
      await renderizarPerfilRecrutador()
    } catch (erro) {
      erroGeral.textContent = mensagemDoErro(erro)
    }
  })

  document.querySelectorAll<HTMLButtonElement>('.btn-curtir-candidato').forEach(botao => {
    botao.addEventListener('click', async () => {
      const candidatoId = Number(botao.dataset.id)
      botao.disabled = true
      try {
        await recrutadorService.curtirCandidato(RECRUTADOR_LOGADO_ID, candidatoId)
        await renderizarPerfilRecrutador()
      } catch (erro) {
        erroGeral.textContent = mensagemDoErro(erro)
        botao.disabled = false
      }
    })
  })
}

async function carregar<T>(carregador: () => Promise<T>, idErro: string): Promise<T | null> {
  try {
    return await carregador()
  } catch (erro) {
    document.getElementById(idErro)!.textContent = mensagemDoErro(erro)
    return null
  }
}

function renderizarVagas(vagas: VagaInterface[] | null): void {
  const tabela = document.getElementById('lista-vagas')!
  if (vagas === null) return
  tabela.innerHTML = vagas.map(vaga => `
    <tr>
      <td>${escaparHtml(vaga.nome ?? vaga.titulo ?? '')}</td>
      <td>${escaparHtml(vaga.descricao)}</td>
      <td>${vaga.competencias.map(escaparHtml).join(', ')}</td>
    </tr>
  `).join('')
}

function renderizarCandidatos(candidatos: CandidatoDisponivelInterface[] | null): void {
  const tabela = document.getElementById('lista-candidatos')!
  if (candidatos === null) return
  tabela.innerHTML = candidatos.map(candidato => `
    <tr>
      <td>${escaparHtml(candidato.descricao)}</td>
      <td>${candidato.competencias.map(escaparHtml).join(', ')}</td>
      <td>
        <button class="btn-curtir-candidato" data-id="${escaparHtml(String(candidato.id))}">
          Curtir
        </button>
      </td>
    </tr>
  `).join('')
}

function renderizarMatches(matches: MatchInterface[] | null): void {
  const tabela = document.getElementById('lista-matches')!
  if (matches === null) return
  tabela.innerHTML = matches.map(match => `
    <tr>
      <td>
        ${escaparHtml(match.empresaNome)}<br>
        ${escaparHtml(match.empresaDescricao)}<br>
        ${escaparHtml(match.empresaEmail)}
      </td>
      <td>
        ${escaparHtml(match.recrutadorNome ?? '')}<br>
        ${escaparHtml(match.recrutadorAreaRecrutamento ?? '')}<br>
        ${escaparHtml(match.recrutadorEmail ?? '')}
      </td>
      <td>
        ${escaparHtml(match.candidatoNome)}<br>
        ${escaparHtml(String(match.candidatoIdade))} anos<br>
        ${escaparHtml(match.candidatoEmail)}
      </td>
    </tr>
  `).join('')
}

function mensagemDoErro(erro: unknown): string {
  return erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
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
