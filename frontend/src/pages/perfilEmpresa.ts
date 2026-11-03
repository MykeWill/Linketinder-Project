import { Chart } from 'chart.js/auto'
import * as candidatoService from '../service/candidatoService'
import * as empresaService from '../service/empresaService'
import * as recrutadorService from '../service/recrutadorService'
import * as vagaService from '../service/vagaService'
import { obterSessao, salvarSessao } from '../service/sessaoService'
import type {
  PerfilEmpresaInterface,
  RecrutadorInterface,
  VagaInterface
} from '../types/models'

export async function renderizarPerfilEmpresa() {
  const sessao = obterSessao()
  if (!sessao || sessao.tipo !== 'EMPRESA') {
    window.location.hash = '#/login'
    return
  }

  const empresaId = sessao.id
  const app = document.getElementById('app')!
  let vagasDaEmpresa: VagaInterface[] = []
  let erroListagemVagas = ''
  let competenciaContagem: Record<string, number> = {}
  let erroContagemCompetencias = ''
  let recrutadores: RecrutadorInterface[] = []
  let erroListagemRecrutadores = ''
  let perfilEmpresa: PerfilEmpresaInterface | null = null
  let erroPerfil = ''

  try {
    perfilEmpresa = await empresaService.buscarPerfilEmpresa(empresaId)
  } catch (erro) {
    erroPerfil = erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
  }

  try {
    vagasDaEmpresa = await vagaService.listarVagasPorEmpresaService(empresaId)
  } catch (erro) {
    erroListagemVagas = erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
  }

  try {
    recrutadores = await recrutadorService.listarPorEmpresa(empresaId)
  } catch (erro) {
    erroListagemRecrutadores =
      erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
  }

  try {
    competenciaContagem = await candidatoService.contarCandidatosPorCompetenciaService()
  } catch (erro) {
    erroContagemCompetencias =
      erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
  }

  const linhasVagas = vagasDaEmpresa.map(vaga => `
        <tr title="${escaparHtml(vaga.descricao)}">
            <td>${escaparHtml(vaga.nome ?? vaga.titulo ?? '')}</td>
            <td>${vaga.competencias.map(escaparHtml).join(', ')}</td>
            <td>
              <button class="btn-editar-vaga" data-id="${escaparHtml(String(vaga.id))}">Editar</button>
              <button class="btn-remover-vaga" data-id="${escaparHtml(String(vaga.id))}">Remover</button>
            </td>
        </tr>
    `).join('')

  const linhasRecrutadores = recrutadores.map(recrutador => `
        <tr>
            <td>${escaparHtml(recrutador.nome)}</td>
            <td>${escaparHtml(recrutador.areaRecrutamento)}</td>
            <td>${escaparHtml(recrutador.email)}</td>
            <td>
              <button class="btn-editar-recrutador" data-id="${escaparHtml(String(recrutador.id))}">Editar</button>
              <button class="btn-remover-recrutador" data-id="${escaparHtml(String(recrutador.id))}">Remover</button>
            </td>
        </tr>
    `).join('')

  app.innerHTML = `
        <h1>Perfil da Empresa</h1>
        <h2>Bem-vindo(a), ${escaparHtml(sessao.nome)}!</h2>
        <button id="btn-editar-perfil" type="button">Editar Perfil</button>
        <form id="form-editar-perfil" class="escondido">
            <label>Nome <input id="perfil-nome" required></label>
            <label>E-mail <input id="perfil-email" type="email" required></label>
            <label>CNPJ <input id="perfil-cnpj" required></label>
            <label>País <input id="perfil-pais" required></label>
            <label>Estado <input id="perfil-estado" required></label>
            <label>CEP <input id="perfil-cep" required></label>
            <label>Descrição <textarea id="perfil-descricao" required></textarea></label>
            <label>Nova senha <input id="perfil-senha" type="password"
              autocomplete="new-password" placeholder="Deixe em branco para manter a senha"></label>
            <button type="submit">Salvar perfil</button>
        </form>
        <p id="erro-perfil" class="mensagem-erro" role="alert">${escaparHtml(erroPerfil)}</p>
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
        <form id="form-editar-vaga" class="escondido">
            <input type="text" id="editar-vaga-titulo" placeholder="Título da vaga" required>
            <textarea id="editar-vaga-descricao" placeholder="Descrição da vaga" required></textarea>
            <input type="text" id="editar-vaga-local" placeholder="Local">
            <input type="text" id="editar-vaga-competencias"
              placeholder="Competências exigidas (separadas por vírgula)" required>
            <button type="submit">Salvar alterações</button>
            <button id="btn-cancelar-edicao-vaga" type="button">Cancelar</button>
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
        <button id="btn-novo-recrutador" class="btn-novo-recrutador" type="button">
          + Cadastrar novo recrutador
        </button>
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
        <form id="form-editar-recrutador" class="escondido">
            <h3>Editar recrutador</h3>
            <label>Nome <input id="editar-recrutador-nome" required></label>
            <label>Área de recrutamento <input id="editar-recrutador-area" required></label>
            <label>Descrição <textarea id="editar-recrutador-descricao" required></textarea></label>
            <label>E-mail <input id="editar-recrutador-email" type="email" required></label>
            <label>Data de nascimento <input id="editar-recrutador-data-nascimento"
              type="date" required></label>
            <label>Nova senha <input id="editar-recrutador-senha" type="password"
              autocomplete="new-password" placeholder="Deixe em branco para manter a senha"></label>
            <button type="submit">Salvar alterações</button>
            <button id="btn-cancelar-edicao-recrutador" type="button">Cancelar</button>
        </form>
        <p id="mensagem-erro" class="mensagem-erro"></p>
        <h2>Candidatos por competência</h2>
        <canvas id="grafico-competencias"></canvas>
        <a href="#/">Voltar</a>
    `

  const mensagemErro = document.getElementById('mensagem-erro')!
  const formularioPerfil = document.getElementById('form-editar-perfil') as HTMLFormElement
  preencherFormularioPerfil(perfilEmpresa)
  document.getElementById('btn-editar-perfil')!.addEventListener('click', () => {
    formularioPerfil.classList.toggle('escondido')
  })
  formularioPerfil.addEventListener('submit', async evento => {
    evento.preventDefault()
    const senha = (document.getElementById('perfil-senha') as HTMLInputElement).value
    try {
      await empresaService.atualizarPerfilEmpresa(empresaId, {
        nome: (document.getElementById('perfil-nome') as HTMLInputElement).value,
        email: (document.getElementById('perfil-email') as HTMLInputElement).value,
        cnpj: (document.getElementById('perfil-cnpj') as HTMLInputElement).value,
        pais: (document.getElementById('perfil-pais') as HTMLInputElement).value,
        estado: (document.getElementById('perfil-estado') as HTMLInputElement).value,
        cep: (document.getElementById('perfil-cep') as HTMLInputElement).value,
        descricao: (document.getElementById('perfil-descricao') as HTMLTextAreaElement).value,
        senha
      })
      salvarSessao({
        ...sessao,
        nome: (document.getElementById('perfil-nome') as HTMLInputElement).value
      })
      await renderizarPerfilEmpresa()
    } catch (erro) {
      document.getElementById('erro-perfil')!.textContent =
        erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
    }
  })
  mensagemErro.textContent = [
    erroListagemVagas,
    erroListagemRecrutadores,
    erroContagemCompetencias
  ]
    .filter(Boolean)
    .join(' ')

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

  formularioVaga.addEventListener('submit', async (evento) => {
    evento.preventDefault()
    const titulo = (document.getElementById('titulo') as HTMLInputElement).value
    const descricao = (document.getElementById('descricao') as HTMLTextAreaElement).value
    const competencias = (document.getElementById('competencias') as HTMLInputElement).value
        .split(',')
        .map(c => c.trim())
        .filter(c => c.length > 0)
    try {
      await vagaService.cadastrarVagaService({ empresaId, titulo, descricao, competencias })
      await renderizarPerfilEmpresa()
    } catch (erro) {
      mensagemErro.textContent = (erro as Error).message
    }
  })

  const formularioEdicaoVaga = document.getElementById('form-editar-vaga') as HTMLFormElement
  let vagaEmEdicao: VagaInterface | null = null
  document.querySelectorAll<HTMLButtonElement>('.btn-editar-vaga').forEach(botao => {
    botao.addEventListener('click', () => {
      vagaEmEdicao = vagasDaEmpresa.find(vaga => String(vaga.id) === botao.dataset.id) ?? null
      if (!vagaEmEdicao) return
      ;(document.getElementById('editar-vaga-titulo') as HTMLInputElement).value =
        vagaEmEdicao.nome ?? vagaEmEdicao.titulo ?? ''
      ;(document.getElementById('editar-vaga-descricao') as HTMLTextAreaElement).value =
        vagaEmEdicao.descricao
      ;(document.getElementById('editar-vaga-local') as HTMLInputElement).value =
        vagaEmEdicao.local ?? ''
      ;(document.getElementById('editar-vaga-competencias') as HTMLInputElement).value =
        vagaEmEdicao.competencias.join(', ')
      formularioEdicaoVaga.classList.remove('escondido')
    })
  })
  document.getElementById('btn-cancelar-edicao-vaga')!.addEventListener('click', () => {
    formularioEdicaoVaga.classList.add('escondido')
    vagaEmEdicao = null
  })
  formularioEdicaoVaga.addEventListener('submit', async evento => {
    evento.preventDefault()
    if (!vagaEmEdicao) return
    try {
      await vagaService.atualizarVagaService({
        id: vagaEmEdicao.id,
        nome: (document.getElementById('editar-vaga-titulo') as HTMLInputElement).value,
        descricao: (document.getElementById('editar-vaga-descricao') as HTMLTextAreaElement).value,
        local: (document.getElementById('editar-vaga-local') as HTMLInputElement).value,
        competencias: (document.getElementById('editar-vaga-competencias') as HTMLInputElement)
          .value.split(',').map(valor => valor.trim()).filter(Boolean)
      })
      await renderizarPerfilEmpresa()
    } catch (erro) {
      mensagemErro.textContent = erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
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
        empresaId,
        nome: (document.getElementById('recrutador-nome') as HTMLInputElement).value,
        areaRecrutamento: (document.getElementById('recrutador-area') as HTMLInputElement).value,
        descricao: (document.getElementById('recrutador-descricao') as HTMLTextAreaElement).value,
        email: (document.getElementById('recrutador-email') as HTMLInputElement).value,
        cpf: (document.getElementById('recrutador-cpf') as HTMLInputElement).value.replace(/\D/g, ''),
        dataNascimento: normalizarDataISO(
          (document.getElementById('recrutador-data-nascimento') as HTMLInputElement).value
        ),
        senha: (document.getElementById('recrutador-senha') as HTMLInputElement).value
      })
      await renderizarPerfilEmpresa()
    } catch (erro) {
      mensagemErro.textContent = (erro as Error).message
    }
  })

  const formularioEdicaoRecrutador =
    document.getElementById('form-editar-recrutador') as HTMLFormElement
  let recrutadorEmEdicao: RecrutadorInterface | null = null
  document.querySelectorAll<HTMLButtonElement>('.btn-editar-recrutador').forEach(botao => {
    botao.addEventListener('click', async () => {
      try {
        const recrutador = await recrutadorService.buscarRecrutador(botao.dataset.id!)
        recrutadorEmEdicao = recrutador
        ;(document.getElementById('editar-recrutador-nome') as HTMLInputElement).value =
          recrutador.nome
        ;(document.getElementById('editar-recrutador-area') as HTMLInputElement).value =
          recrutador.areaRecrutamento
        ;(document.getElementById('editar-recrutador-descricao') as HTMLTextAreaElement).value =
          recrutador.descricao
        ;(document.getElementById('editar-recrutador-email') as HTMLInputElement).value =
          recrutador.email
        ;(document.getElementById('editar-recrutador-data-nascimento') as HTMLInputElement).value =
          recrutador.dataNascimento
        ;(document.getElementById('editar-recrutador-senha') as HTMLInputElement).value = ''
        formularioEdicaoRecrutador.classList.remove('escondido')
      } catch (erro) {
        mensagemErro.textContent = erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
      }
    })
  })

  document.getElementById('btn-cancelar-edicao-recrutador')!.addEventListener('click', () => {
    formularioEdicaoRecrutador.classList.add('escondido')
    recrutadorEmEdicao = null
  })

  formularioEdicaoRecrutador.addEventListener('submit', async evento => {
    evento.preventDefault()
    if (!recrutadorEmEdicao) return
    try {
      await recrutadorService.atualizarRecrutador(recrutadorEmEdicao.id, {
        nome: (document.getElementById('editar-recrutador-nome') as HTMLInputElement).value,
        areaRecrutamento: (document.getElementById('editar-recrutador-area') as HTMLInputElement).value,
        descricao: (document.getElementById('editar-recrutador-descricao') as HTMLTextAreaElement).value,
        email: (document.getElementById('editar-recrutador-email') as HTMLInputElement).value,
        dataNascimento: (document.getElementById(
          'editar-recrutador-data-nascimento'
        ) as HTMLInputElement).value,
        senha: (document.getElementById('editar-recrutador-senha') as HTMLInputElement).value
      })
      await renderizarPerfilEmpresa()
    } catch (erro) {
      mensagemErro.textContent = erro instanceof Error ? erro.message : 'Ocorreu um erro inesperado.'
    }
  })

  document.querySelectorAll('.btn-remover-vaga').forEach(botao => {
    botao.addEventListener('click', async () => {
      const id = (botao as HTMLElement).dataset.id!
      try {
        await vagaService.removerVagaService(id)
        await renderizarPerfilEmpresa()
      } catch (erro) {
        mensagemErro.textContent = (erro as Error).message
      }
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

function preencherFormularioPerfil(perfil: PerfilEmpresaInterface | null): void {
  if (!perfil) return
  ;(['nome', 'email', 'cnpj', 'pais', 'estado', 'cep', 'descricao'] as const).forEach(campo => {
    const elemento = document.getElementById(`perfil-${campo}`)
    if (elemento instanceof HTMLInputElement || elemento instanceof HTMLTextAreaElement) {
      elemento.value = perfil[campo]
    }
  })
}

function normalizarDataISO(data: string): string {
  const dataBrasileira = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(data)
  if (dataBrasileira) {
    return `${dataBrasileira[3]}-${dataBrasileira[2]}-${dataBrasileira[1]}`
  }
  return data
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