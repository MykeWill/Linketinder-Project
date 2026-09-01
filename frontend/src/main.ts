import './style.css'
import { registrarRota, iniciarRoteador } from './router'
import { renderizarCadastro } from './pages/cadastro'
import { renderizarLogin } from './pages/login'
import { renderizarPerfilCandidato } from './pages/perfilCandidato'
import { renderizarPerfilEmpresa } from './pages/perfilEmpresa'
import { renderizarCadastroCandidato } from './pages/cadastroCandidato'
import { renderizarCadastroEmpresa } from './pages/cadastroEmpresa'

registrarRota('/', renderizarCadastro)
registrarRota('/login', renderizarLogin)
registrarRota('/perfil-candidato', renderizarPerfilCandidato)
registrarRota('/perfil-empresa', renderizarPerfilEmpresa)
registrarRota('/cadastro-candidato', renderizarCadastroCandidato)
registrarRota('/cadastro-empresa', renderizarCadastroEmpresa)

iniciarRoteador()
