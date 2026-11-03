import * as candidatoService from '../service/candidatoService'
import * as vagaService from '../service/vagaService'
import { obterSessao, salvarSessao } from '../service/sessaoService'
import type {
  MatchCandidatoInterface,
  PerfilCandidatoInterface,
  VagaAnonimaInterface,
  VagaResumoAnonimaInterface
} from '../types/models'

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
    <button id="btn-editar-perfil" type="button">Editar Perfil</button>
    <p id="erro-perfil" class="mensagem-erro" role="alert"></p>
    <form id="form-editar-perfil" class="escondido">
      <label>Nome <input id="perfil-nome" required></label>
      <label>E-mail <input id="perfil-email" type="email" required></label>
      <label>CPF <input id="perfil-cpf" required></label>
      <label>Idade <input id="perfil-idade" type="number" min="16" max="120" required></label>
      <label>Estado <input id="perfil-estado" required></label>
      <label>CEP <input id="perfil-cep" required></label>
      <label>Descrição <textarea id="perfil-descricao" required></textarea></label>
      <label>Competências <input id="perfil-competencias"></label>
      <label>Nova senha <input id="perfil-senha" type="password"
        autocomplete="new-password" placeholder="Deixe em branco para manter a senha"></label>
      <button type="submit">Salvar perfil</button>
    </form>
    <section>
      <h2>Vagas disponíveis</h2>
      <p id="erro-vagas" class="mensagem-erro" role="alert"></p>
      <table>
        <thead>
          <tr><th>Nome</th><th>Descrição</th><th>Competências</th><th>Afinidade</th><th>Ações</th></tr>
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

  const [perfil, vagas, curtidas, matches] = await Promise.all([
    carregar(() => candidatoService.buscarPerfilCandidato(candidatoId), 'erro-perfil'),
    carregar(() => vagaService.listarVagasAnonimas(candidatoId), 'erro-vagas'),
    carregar(() => candidatoService.listarCurtidasCandidato(candidatoId), 'erro-curtidas'),
    carregar(
      () => candidatoService.listarMatchesCandidatoService(candidatoId),
      'erro-matches'
    )
  ])
  configurarEdicaoPerfil(perfil, candidatoId, sessao)
  renderizarVagas(vagas, candidatoId)
  renderizarCurtidas(curtidas)
  renderizarMatches(matches)
}

function configurarEdicaoPerfil(
  perfil: PerfilCandidatoInterface | null,
  candidatoId: number,
  sessao: NonNullable<ReturnType<typeof obterSessao>>
): void {
  const formulario = document.getElementById('form-editar-perfil') as HTMLFormElement
  formulario.querySelectorAll<HTMLInputElement | HTMLTextAreaElement>('[id^="perfil-"]')
    .forEach(campo => {
      const propriedade = campo.id.replace('perfil-', '') as keyof PerfilCandidatoInterface
      if (perfil && propriedade in perfil) {
        const valor = perfil[propriedade]
        campo.value = Array.isArray(valor) ? valor.join(', ') : String(valor ?? '')
      }
    })

  document.getElementById('btn-editar-perfil')!.addEventListener('click', () => {
    formulario.classList.toggle('escondido')
  })

  formulario.addEventListener('submit', async evento => {
    evento.preventDefault()
    const erro = document.getElementById('erro-perfil')!
    erro.textContent = ''
    try {
      const senha = (document.getElementById('perfil-senha') as HTMLInputElement).value
      await candidatoService.atualizarPerfilCandidato(candidatoId, {
        nome: (document.getElementById('perfil-nome') as HTMLInputElement).value,
        email: (document.getElementById('perfil-email') as HTMLInputElement).value,
        cpf: (document.getElementById('perfil-cpf') as HTMLInputElement).value,
        idade: Number((document.getElementById('perfil-idade') as HTMLInputElement).value),
        estado: (document.getElementById('perfil-estado') as HTMLInputElement).value,
        cep: (document.getElementById('perfil-cep') as HTMLInputElement).value,
        descricao: (document.getElementById('perfil-descricao') as HTMLTextAreaElement).value,
        competencias: (document.getElementById('perfil-competencias') as HTMLInputElement)
          .value.split(',').map(valor => valor.trim()).filter(Boolean),
        senha
      })
      salvarSessao({ ...sessao, nome: (document.getElementById('perfil-nome') as HTMLInputElement).value })
      await renderizarPerfilCandidato()
    } catch (falha) {
      erro.textContent = falha instanceof Error ? falha.message : 'Ocorreu um erro inesperado.'
    }
  })
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
      <td><span class="afinidade ${classeAfinidade(vaga.afinidade)}">${Math.round(vaga.afinidade)}%</span></td>
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
        botao.closest('tr')?.remove()
        if (match) alert('Deu Match!')

        const [curtidasAtualizadas, matchesAtualizados] = await Promise.all([
          carregar(
            () => candidatoService.listarCurtidasCandidato(candidatoId),
            'erro-curtidas'
          ),
          carregar(
            () => candidatoService.listarMatchesCandidatoService(candidatoId),
            'erro-matches'
          )
        ])
        renderizarCurtidas(curtidasAtualizadas)
        renderizarMatches(matchesAtualizados)
      } catch (erro) {
        document.getElementById('erro-vagas')!.textContent =
          erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
        if (botao.isConnected) botao.disabled = false
      }
    })
  })
}

function classeAfinidade(afinidade: number): string {
  if (afinidade > 70) return 'afinidade-alta'
  if (afinidade >= 40) return 'afinidade-media'
  return 'afinidade-baixa'
}

function renderizarCurtidas(curtidas: VagaResumoAnonimaInterface[] | null): void {
  if (curtidas === null) return
  const tbody = document.getElementById('lista-curtidas')!
  tbody.innerHTML = ''
  if (curtidas.length === 0) {
    tbody.innerHTML = '<tr><td colspan="3">Nenhuma curtida ainda.</td></tr>'
    return
  }
  curtidas.forEach(adicionarVagaCurtida)
}

function adicionarVagaCurtida(vaga: VagaResumoAnonimaInterface): void {
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