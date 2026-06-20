package br.com.prefeitura.eventos.model;

import java.time.LocalDateTime;

public class LocalEvento {
    private Long id;
    private String nome;
    private String endereco;
    private Integer capacidade;
    private String descricao;
    private LocalDateTime criadoEm;

    public LocalEvento() {}

    public LocalEvento(String nome, String endereco, Integer capacidade, String descricao) {
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidade;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return this.nome + " (Capacidade: " + this.capacidade + ")"; // Exibe: Auditório Principal (Capacidade: 50)
    }

}