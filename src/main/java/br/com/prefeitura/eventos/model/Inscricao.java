package br.com.prefeitura.eventos.model;

import br.com.prefeitura.eventos.model.enums.StatusInscricao;
import java.time.LocalDateTime;

public class Inscricao {
    private Long id;
    private Long eventoId;
    private Long usuarioId;
    private LocalDateTime dataInscricao;
    private StatusInscricao status;
    private String observacao;

    public Inscricao() {}

    public Inscricao(Long eventoId, Long usuarioId, String observacao) {
        this.eventoId = eventoId;
        this.usuarioId = usuarioId;
        this.observacao = observacao;
        this.status = StatusInscricao.PENDENTE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public StatusInscricao getStatus() {
        return status;
    }

    public void setStatus(StatusInscricao status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}