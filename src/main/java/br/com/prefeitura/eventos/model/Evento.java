package br.com.prefeitura.eventos.model;

import br.com.prefeitura.eventos.model.enums.StatusEvento;
import java.time.LocalDateTime;

public class Evento {
    private Long id;
    private String titulo;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Long localId; 
    private Integer capacidade;
    private String categoria;
    private Long organizadorId;
    
    private StatusEvento status;
    private LocalDateTime criadoEm;

    public Evento() {}

    public Evento(String titulo, String descricao, LocalDateTime dataInicio, LocalDateTime dataFim, 
                  Long localId, Integer capacidade, String categoria, Long organizadorId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.localId = localId;
        this.capacidade = capacidade;
        this.categoria = categoria;
        this.organizadorId = organizadorId;
        this.status = StatusEvento.AGENDADO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getOrganizadorId() {
        return organizadorId;
    }

    public void setOrganizadorId(Long organizadorId) {
        this.organizadorId = organizadorId;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public void setStatus(StatusEvento status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

}