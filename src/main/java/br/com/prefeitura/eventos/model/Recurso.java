package br.com.prefeitura.eventos.model;

public class Recurso {
    private Long id;
    private String nome;
    private String tipo;
    private Integer capacidade;
    private String descricao;

    public Recurso() {}

    public Recurso(String nome, String tipo, Integer capacidade, String descricao) {
        this.nome = nome;
        this.tipo = tipo;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

}