export interface CandidatoInterface {
  id: string
  nome: string
  email: string
  cpf: string
  idade: number
  estado: string
  cep: string
  descricao: string
  competencias: string[]
  formacao: string
}

export interface EmpresaInterface {
  id: string
  nome: string
  emailCorporativo: string
  cnpj: string
  pais: string
  estado: string
  cep: string
  descricao: string
}

export interface VagaInterface {
  id: string
  empresaId: string
  titulo: string
  descricao: string
  competencias: string[]
}