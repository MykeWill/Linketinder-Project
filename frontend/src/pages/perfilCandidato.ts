import * as candidatoService from '../service/candidatoService'
import * as vagaService from '../service/vagaService'
import type { MatchCandidatoInterface, VagaAnonimaInterface } from '../types/models'

const CANDIDATO_LOGADO_ID = 1

export async function renderizarPerfilCandidato(): Promise<void> {
  const app = document.getElementById('app')!
  app.innerHTML = `
    <h1>Perfil do Candidato</h1>
    <section>
      <h2>Vagas disponíveis</h2>
      <p id="erro-vagas" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr><th>Nome</th><th>Descrição</th><th>Competências</th></tr>
        </thead>
        <tbody id="lista-vagas"></tbody>
      </table>
    </section>
    <section>
      <h2>Meus Matches</h2>
      <p id="erro-matches" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr><th>Empresa</th><th>Contato da empresa</th><th>Recrutador</th></tr>
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
    </tr>
  `).join('')
}

function renderizarMatches(matches: MatchCandidatoInterface[] | null): void {
  if (matches === null) return
  document.getElementById('lista-matches')!.innerHTML = matches.map(match => `
    <tr>
      <td>
        ${escaparHtml(match.empresaNome)}<br>
        ${escaparHtml(match.empresaDescricao)}
      </td>
      <td>${escaparHtml(match.empresaEmail)}</td>
      <td>
        ${escaparHtml(match.recrutadorNome ?? 'Recrutador não disponível')}<br>
        ${match.recrutadorId === null ? '' : `ID: ${escaparHtml(String(match.recrutadorId))}<br>`}
        ${escaparHtml(match.recrutadorAreaRecrutamento ?? '')}<br>
        ${escaparHtml(match.recrutadorEmail ?? '')}
      </td>
    </tr>
  `).join('')
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