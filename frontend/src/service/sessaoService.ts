import type { SessaoInterface, TipoUsuario } from '../types/models'

const CHAVE_SESSAO = 'linketinder:sessao'
const TIPOS_USUARIO: TipoUsuario[] = ['CANDIDATO', 'EMPRESA', 'RECRUTADOR']

export function salvarSessao(sessao: SessaoInterface): void {
  localStorage.setItem(CHAVE_SESSAO, JSON.stringify(sessao))
}

export function obterSessao(): SessaoInterface | null {
  const dados = localStorage.getItem(CHAVE_SESSAO)
  if (!dados) return null

  try {
    return validarSessao(JSON.parse(dados) as unknown)
  } catch {
    return null
  }
}

export function validarSessao(dados: unknown): SessaoInterface | null {
  return isSessao(dados) ? dados : null
}

function isSessao(dados: unknown): dados is SessaoInterface {
  if (
    typeof dados !== 'object' ||
    dados === null ||
    !('id' in dados) ||
    typeof dados.id !== 'number' ||
    !Number.isInteger(dados.id) ||
    dados.id <= 0 ||
    !('token' in dados) ||
    typeof dados.token !== 'string' ||
    dados.token.length === 0 ||
    !('nome' in dados) ||
    typeof dados.nome !== 'string' ||
    !('tipo' in dados) ||
    !isTipoUsuario(dados.tipo)
  ) {
    return false
  }

  if (
    dados.tipo === 'RECRUTADOR' &&
    (!('empresaId' in dados) ||
      typeof dados.empresaId !== 'number' ||
      !Number.isInteger(dados.empresaId) ||
      dados.empresaId <= 0)
  ) {
    return false
  }

  return true
}

function isTipoUsuario(tipo: unknown): tipo is TipoUsuario {
  return typeof tipo === 'string' && TIPOS_USUARIO.some(tipoValido => tipoValido === tipo)
}
