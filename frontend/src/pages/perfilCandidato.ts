import * as candidatoService from '../service/candidatoService'
import * as vagaService from '../service/vagaService'
import type { MatchCandidatoInterface, VagaAnonimaInterface } from '../types/models'

const CANDIDATO_LOGADO_ID = 1

export async function renderizarPerfilCandidato(): Promise<void> {
  const app = document.getElementById('app')!
  app.innerHTML = `
    <h1>Perfil do Candidato</h1>
    <p style="color: red; font-weight: bold;">[VERSAO NOVA 2.0]</p>
    <section>
      <h2>Vagas disponíveis</h2>
      <p id="erro-vagas" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr><th>Nome</th><th>Descrição</th><th>Competências</th><th>Ações</th></tr>
        </thead>
        <tbody id="lista-vagas"></tbody>
      </table>
    </section>
    <section>
      <h2>Meus Matches</h2>
      <p id="erro-matches" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr><th>Vaga</th><th>Empresa / contato</th><th>Recrutador</th></tr>
        </thead>
        <tbody id="lista-matches"></tbody>
      </table>
    </section>
    <a href="#/">Voltar</a>
  `

  const [vagas, matches] = await Promise.all([
    carregar(() => vagaService.listarVagasAnonimas(), 'erro-vagas'),
    carregar(
      () => candidatoService.listarMatchesCandidatoService(CANDIDATO_LOGADO_ID),
      'erro-matches'
    )
  ])
  renderizarVagas(vagas)
  renderizarMatches(matches)
}

async function carregar<T>(carregador: () => Promise<T>, idErro: string): Promise<T | null> {
  try {
    return await carregador()
  } catch (erro) {
    document.getElementById(idErro)!.textContent =
      erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
    return null
  }
}

function renderizarVagas(vagas: VagaAnonimaInterface[] | null): void {
  if (vagas === null) return
  document.getElementById('lista-vagas')!.innerHTML = vagas.map(vaga => `
    <tr>
      <td>${escaparHtml(vaga.nome)}</td>
      <td>${escaparHtml(vaga.descricao)}</td>
      <td>${vaga.competencias.map(escaparHtml).join(', ')}</td>
      <td><button class="btn-curtir-vaga" data-id="${escaparHtml(String(vaga.id))}">Curtir</button></td>
    </tr>
  `).join('')

  document.querySelectorAll<HTMLButtonElement>('.btn-curtir-vaga').forEach(botao => {
    botao.addEventListener('click', async () => {
      botao.disabled = true
      document.getElementById('erro-vagas')!.textContent = ''
      try {
        const { match } = await candidatoService.curtirVaga(
          CANDIDATO_LOGADO_ID,
          Number(botao.dataset.id)
        )
        if (match) {
          alert('Deu Match!')
          const matchesAtualizados = await candidatoService.listarMatchesCandidatoService(CANDIDATO_LOGADO_ID)
          console.log("DADOS DO FETCH DO MATCH:", matchesAtualizados)
          renderizarMatches(matchesAtualizados)
        } else {
          botao.textContent = 'Curtido'
        }
      } catch (erro) {
        document.getElementById('erro-vagas')!.textContent =
          erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
        botao.disabled = false
      }
    })
  })
}

function renderizarMatches(matches: MatchCandidatoInterface[] | null): void {
  console.log("DADOS RECEBIDOS PARA RENDERIZAR MATCHES:", matches)
  const tbody = document.getElementById('lista-matches')
  if (!tbody) {
    console.error('Elemento #lista-matches não encontrado.')
    return
  }

  if (!matches || !Array.isArray(matches)) {
    tbody.innerHTML = '<tr><td colspan="3">Nenhum match ainda.</td></tr>'
    return
  }

  tbody.innerHTML = ''
  matches.forEach(match => {
    const tr = document.createElement('tr')

    const tdVaga = document.createElement('td')
    tdVaga.textContent = `${match.vagaNome || 'Vaga'} - ${match.vagaDescricao || ''}`

    const tdEmpresa = document.createElement('td')
    tdEmpresa.textContent = `${match.empresaNome || 'Empresa'} - ${match.empresaDescricao || ''} - ${match.empresaEmail || 'Sem e-mail'}`

    const tdRecrutador = document.createElement('td')
    tdRecrutador.textContent = [
      match.recrutadorNome || 'Recrutador não informado',
      match.recrutadorId === null ? '' : `ID: ${match.recrutadorId}`,
      match.recrutadorAreaRecrutamento || '',
      match.recrutadorEmail || ''
    ].filter(Boolean).join(' - ')

    tr.appendChild(tdVaga)
    tr.appendChild(tdEmpresa)
    tr.appendChild(tdRecrutador)
    tbody.appendChild(tr)
  })
}

function escaparHtml(valor: string | null | undefined): string {
  if (valor === null || valor === undefined) return ''
  return valor.replace(/[&<>"']/g, caractere => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  })[caractere]!)
}