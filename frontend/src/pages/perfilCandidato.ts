import * as candidatoService from '../service/candidatoService'
import * as vagaService from '../service/vagaService'
import { obterSessao } from '../service/sessaoService'
import type { MatchCandidatoInterface, VagaAnonimaInterface } from '../types/models'

export async function renderizarPerfilCandidato(): Promise<void> {
  const sessao = obterSessao()
  if (!sessao || sessao.tipo !== 'CANDIDATO') {
    window.location.hash = '#/login'
    return
  }

  const candidatoId = sessao.id
  const app = document.getElementById('app')!
  app.innerHTML = `
    <h1>Perfil do Candidato</h1>
    <h2>Bem-vindo(a), ${escaparHtml(sessao.nome)}!</h2>
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
      <h2>Minhas Curtidas</h2>
      <p id="erro-curtidas" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr><th>Nome</th><th>Descrição</th><th>Competências</th></tr>
        </thead>
        <tbody id="lista-curtidas"></tbody>
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

  const [vagas, curtidas, matches] = await Promise.all([
    carregar(() => vagaService.listarVagasAnonimas(candidatoId), 'erro-vagas'),
    carregar(() => candidatoService.listarCurtidasCandidato(candidatoId), 'erro-curtidas'),
    carregar(
      () => candidatoService.listarMatchesCandidatoService(candidatoId),
      'erro-matches'
    )
  ])
  renderizarVagas(vagas, candidatoId)
  renderizarCurtidas(curtidas)
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

function renderizarVagas(vagas: VagaAnonimaInterface[] | null, candidatoId: number): void {
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
          candidatoId,
          Number(botao.dataset.id)
        )
        const vagaCurtida = vagas.find(vaga => String(vaga.id) === botao.dataset.id)
        botao.closest('tr')?.remove()
        if (vagaCurtida) adicionarVagaCurtida(vagaCurtida)
        if (match) {
          alert('Deu Match!')
          const matchesAtualizados = await candidatoService.listarMatchesCandidatoService(candidatoId)
          console.log("DADOS DO FETCH DO MATCH:", matchesAtualizados)
          renderizarMatches(matchesAtualizados)
        }
      } catch (erro) {
        document.getElementById('erro-vagas')!.textContent =
          erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
        if (botao.isConnected) botao.disabled = false
      }
    })
  })
}

function renderizarCurtidas(curtidas: VagaAnonimaInterface[] | null): void {
  if (curtidas === null) return
  const tbody = document.getElementById('lista-curtidas')!
  tbody.innerHTML = ''
  if (curtidas.length === 0) {
    tbody.innerHTML = '<tr><td colspan="3">Nenhuma curtida ainda.</td></tr>'
    return
  }
  curtidas.forEach(adicionarVagaCurtida)
}

function adicionarVagaCurtida(vaga: VagaAnonimaInterface): void {
  const tbody = document.getElementById('lista-curtidas')
  if (!tbody) return
  tbody.querySelector('td[colspan="3"]')?.closest('tr')?.remove()
  const tr = document.createElement('tr')
  const tdNome = document.createElement('td')
  const tdDescricao = document.createElement('td')
  const tdCompetencias = document.createElement('td')
  tdNome.textContent = vaga.nome
  tdDescricao.textContent = vaga.descricao
  tdCompetencias.textContent = vaga.competencias.join(', ')
  tr.append(tdNome, tdDescricao, tdCompetencias)
  tbody.appendChild(tr)
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