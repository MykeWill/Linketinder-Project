package com.linketinder.service

import com.linketinder.exception.ErroBancoException
import com.linketinder.exception.MensagensErro
import com.linketinder.model.Vaga
import com.linketinder.repository.CompetenciaRepository
import com.linketinder.repository.VagaRepository

import java.sql.SQLException

class VagaService {

    private final VagaRepository vagaDao
    private final CompetenciaRepository competenciaDao

    VagaService(VagaRepository vagaDao, CompetenciaRepository competenciaDao) {
        this.vagaDao = vagaDao
        this.competenciaDao = competenciaDao
    }

    Integer cadastrarVagaService(Vaga v) {
        return cadastrarVagaService(v, v.recrutadorId)
    }

    Integer cadastrarVagaService(Vaga v, Integer recrutadorId) {
        v.recrutadorId = recrutadorId
        try {
            Integer vagaId = vagaDao.inserirVaga(v)

            v.competencias.each { nomeCompetencia ->
                Integer competenciaId = competenciaDao.buscarOuCriar(nomeCompetencia)
                vagaDao.vincularCompetenciaAVaga(vagaId, competenciaId)
            }

            return vagaId
        } catch (SQLException e) {
            e.printStackTrace()
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, e)
        }
    }

    List<Vaga> listarTodasVagasService() {
        try {
            return vagaDao.listarTodasVagas()
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    List<Vaga> listarVagasNaoCurtidasPorCandidato(Integer candidatoId) {
        try {
            return vagaDao.listarVagasNaoCurtidasPorCandidato(candidatoId)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    List<Vaga> listarVagasPorEmpresaService(Integer empresaId) {
        try {
            return vagaDao.listarVagasPorEmpresa(empresaId)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    List<Vaga> listarVagasVisiveisParaRecrutador(Integer empresaId) {
        try {
            return vagaDao.listarVagasVisiveisParaRecrutador(empresaId)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    boolean atualizarVagaService(Vaga v, String tipoEditor, Integer editorId) {
        try {
            if (v == null || v.id == null || v.id <= 0 || editorId == null || editorId <= 0 ||
                    !(tipoEditor in ['EMPRESA', 'RECRUTADOR']) ||
                    !v.nome?.trim() || !v.descricao?.trim() ||
                    !(v.competencias instanceof List) ||
                    v.competencias.any { !it?.trim() }) {
                throw new IllegalArgumentException('Dados inválidos para atualizar a vaga.')
            }
            boolean pertenceAoEditor = tipoEditor == 'EMPRESA'
                    ? vagaDao.vagaPertenceEmpresa(v.id, editorId)
                    : vagaDao.vagaPertenceRecrutador(v.id, editorId)
            if (!pertenceAoEditor) {
                return false
            }
            List<Integer> competenciaIds = v.competencias.collect { String nome ->
                competenciaDao.buscarOuCriar(nome.trim())
            }
            return vagaDao.atualizarVaga(v, tipoEditor, editorId, competenciaIds)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }

    void removerVagaService(Integer id) {
        try {
            vagaDao.removerVaga(id)
        } catch (SQLException ex) {
            throw new ErroBancoException(MensagensErro.ERRO_BANCO, ex)
        }
    }
}