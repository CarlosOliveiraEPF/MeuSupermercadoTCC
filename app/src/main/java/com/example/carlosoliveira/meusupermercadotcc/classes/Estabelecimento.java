package com.example.carlosoliveira.meusupermercadotcc.classes;

/**
 * Created by carlos.oliveira on 28/07/2017.
 */

public class Estabelecimento {

    private String id;
    private String nome;
    private String site;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;

    public Estabelecimento() {
    }

    public Estabelecimento(String id, String nome, String logradouro, String numero) {
        this.id = id;
        this.nome = nome;
        this.logradouro = logradouro;
        this.numero = numero;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    @Override
    public String toString() {
        return nome + " - " + logradouro + " nº "+ numero;
    }
}
