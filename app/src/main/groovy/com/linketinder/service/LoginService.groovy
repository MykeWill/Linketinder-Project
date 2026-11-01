package com.linketinder.service

import com.linketinder.exception.DadosInvalidosException
import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.model.Candidato
import com.linketinder.model.Empresa
import com.linketinder.model.Recrutador
import com.linketinder.repository.CandidatoRepository
import com.linketinder.repository.EmpresaRepository
import com.linketinder.repository.RecrutadorRepository
import org.mindrot.jbcrypt.BCrypt

import java.sql.SQLException

class LoginService {

    private final CandidatoRepository candidatoRepository
    private final EmpresaRepository empresaRepository
    private final RecrutadorRepository recrutadorRepository

    LoginService(
            CandidatoRepository candidatoRepository,
            EmpresaRepository empresaRepository,
            RecrutadorRepository recrutadorRepository
    ) {
        this.candidatoRepository = candidatoRepository
        this.empresaRepository = empresaRepository
        this.recrutadorRepository = recrutadorRepository
    }

    Map autenticar(String email, String senha, String tipo) {
        if (!email?.trim() || !senha || !tipo?.trim()) {
            throw new DadosInvalidosException(MensagensErro.CREDENCIAIS_INVALIDAS)
        }

        try {
            switch (tipo.trim().toLowerCase(Locale.ROOT)) {
                case 'candidato':
                    return autenticarCandidato(email, senha)
                case 'empresa':
                    return autenticarEmpresa(email, senha)
                case 'recrutador':
                    return autenticarRecrutador(email, senha)
                default:
                    throw new DadosInvalidosException(MensagensErro.CREDENCIAIS_INVALIDAS)
            }
        } catch (SQLException e) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, e)
        }
    }

    private Map autenticarCandidato(String email, String senha) throws SQLException {
        Candidato candidato = candidatoRepository.buscarPorEmail(email)
        if (candidato == null || candidato.senha != senha) {
            throw new DadosInvalidosException(MensagensErro.CREDENCIAIS_INVALIDAS)
        }
        return [id: candidato.id, nome: candidato.nome, tipo: 'CANDIDATO']
    }

    private Map autenticarEmpresa(String email, String senha) throws SQLException {
        Empresa empresa = empresaRepository.buscarPorEmail(email)
        if (empresa == null || empresa.senha != senha) {
            throw new DadosInvalidosException(MensagensErro.CREDENCIAIS_INVALIDAS)
        }
        return [id: empresa.id, nome: empresa.nome, tipo: 'EMPRESA']
    }

    private Map autenticarRecrutador(String email, String senha) throws SQLException {
        Recrutador recrutador = recrutadorRepository.buscarPorEmail(email)
        if (recrutador == null || !BCrypt.checkpw(senha, recrutador.senhaHash)) {
            throw new DadosInvalidosException(MensagensErro.CREDENCIAIS_INVALIDAS)
        }
        return [
                id       : recrutador.id,
                nome     : recrutador.nome,
                tipo     : 'RECRUTADOR',
                empresaId: recrutador.empresaId
        ]
    }
}
