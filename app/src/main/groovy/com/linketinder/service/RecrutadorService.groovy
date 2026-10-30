package com.linketinder.service

import com.linketinder.exception.DadosInvalidosException
import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.exception.RegistroDuplicadoException
import com.linketinder.exception.RegistroNaoEncontradoException
import com.linketinder.model.Candidato
import com.linketinder.model.Recrutador
import com.linketinder.repository.RecrutadorRepository
import org.mindrot.jbcrypt.BCrypt

import java.sql.SQLException
import java.time.LocalDate
import java.util.regex.Pattern

class RecrutadorService {

    private static final Pattern EMAIL_VALIDO =
            Pattern.compile('^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$')

    private final RecrutadorRepository recrutadorRepository

    RecrutadorService(RecrutadorRepository recrutadorRepository) {
        this.recrutadorRepository = recrutadorRepository
    }

    Integer cadastrarRecrutador(Recrutador recrutador, String senhaPura) {
        validarDados(recrutador, senhaPura)

        try {
            if (recrutadorRepository.existeEmail(recrutador.email)) {
                throw new RegistroDuplicadoException(MensagensErro.EMAIL_DUPLICADO)
            }
            if (recrutadorRepository.existeCpf(recrutador.cpf)) {
                throw new RegistroDuplicadoException(MensagensErro.CPF_DUPLICADO)
            }

            recrutador.senhaHash = BCrypt.hashpw(senhaPura, BCrypt.gensalt())
            return recrutadorRepository.inserirRecrutador(recrutador)
        } catch (SQLException e) {
            throw traduzirErro(e)
        }
    }

    List<Candidato> listarCandidatosDisponiveis(Integer recrutadorId) {
        try {
            return recrutadorRepository.listarCandidatosDisponiveis(recrutadorId)
        } catch (SQLException e) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, e)
        }
    }

    void curtirCandidato(Integer recrutadorId, Integer candidatoId) {
        try {
            Recrutador recrutador = recrutadorRepository.buscarRecrutadorPorId(recrutadorId)
            if (recrutador == null) {
                throw new RegistroNaoEncontradoException(MensagensErro.RECRUTADOR_NAO_ENCONTRADO)
            }
            if (recrutadorRepository.empresaJaCurtiuCandidato(recrutador.empresaId, candidatoId)) {
                throw new RegistroDuplicadoException(MensagensErro.CURTIDA_DUPLICADA)
            }
            recrutadorRepository.curtirCandidato(recrutador.empresaId, candidatoId, recrutadorId)
        } catch (SQLException e) {
            if (e.sqlState == '23505') {
                throw new RegistroDuplicadoException(MensagensErro.CURTIDA_DUPLICADA)
            }
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, e)
        }
    }

    private void validarDados(Recrutador recrutador, String senhaPura) {
        if (recrutador == null || recrutador.email == null ||
                !EMAIL_VALIDO.matcher(recrutador.email).matches() ||
                senhaPura == null || senhaPura.isEmpty()) {
            throw new DadosInvalidosException(MensagensErro.DADOS_INVALIDOS)
        }
        if (!cpfValido(recrutador.cpf)) {
            throw new DadosInvalidosException(MensagensErro.DADOS_INVALIDOS)
        }
        if (recrutador.dataNascimento == null ||
                recrutador.dataNascimento.plusYears(18).isAfter(LocalDate.now())) {
            throw new DadosInvalidosException(MensagensErro.DADOS_INVALIDOS)
        }
    }

    private boolean cpfValido(String cpf) {
        if (cpf == null || !(cpf ==~ /(?:\d{11}|\d{3}\.\d{3}\.\d{3}-\d{2})/)) {
            return false
        }

        String digitos = cpf.replaceAll('\\D', '')
        if (digitos.toSet().size() == 1) {
            return false
        }

        int primeiroDigito = calcularDigito(cpf, 9)
        int segundoDigito = calcularDigito(cpf, 10)
        return digitos.substring(9, 10).toInteger() == primeiroDigito &&
                digitos.substring(10, 11).toInteger() == segundoDigito
    }

    private int calcularDigito(String cpf, int quantidadeDigitos) {
        String digitos = cpf.replaceAll('\\D', '')
        int soma = 0
        for (int indice = 0; indice < quantidadeDigitos; indice++) {
            soma += digitos.substring(indice, indice + 1).toInteger() *
                    (quantidadeDigitos + 1 - indice)
        }
        int resto = soma % 11
        return resto < 2 ? 0 : 11 - resto
    }

    private RuntimeException traduzirErro(SQLException e) {
        String mensagem = e.message?.toLowerCase() ?: ''
        if (mensagem.contains('recrutador_email_key')) {
            return new RegistroDuplicadoException(MensagensErro.EMAIL_DUPLICADO)
        }
        if (mensagem.contains('recrutador_cpf_key')) {
            return new RegistroDuplicadoException(MensagensErro.CPF_DUPLICADO)
        }
        return new ErroBancoException(MensagensErro.ERRO_BANCO, e)
    }
}
