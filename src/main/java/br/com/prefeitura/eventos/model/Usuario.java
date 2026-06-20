package br.com.prefeitura.eventos.model;

import br.com.prefeitura.eventos.model.enums.TipoUsuario;
import java.time.LocalDateTime;

public class Usuario {
    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private TipoUsuario tipo;
    private String telefone;
    private LocalDateTime criadoEm;

    public Usuario() {}

    public Usuario(String nome, String email, String senhaHash, TipoUsuario tipo, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipo = tipo;
        this.telefone = telefone;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return this.nome + " - " + this.tipo.name(); // Exibe: João Organizador - ORGANIZADOR
    }


}