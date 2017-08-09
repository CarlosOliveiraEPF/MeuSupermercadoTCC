package com.example.carlosoliveira.meusupermercadotcc.classes;

/**
 * Created by carlos.oliveira on 30/07/2017.
 */

public class Produto {

    private String id;
    private String produto;
    private String qtd;
    private String idUser;

    public Produto(String id, String produto, String qtd, String idUser) {
        this.id = id;
        this.produto = produto;
        this.qtd = qtd;
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getQtd() {
        return qtd;
    }

    public void setQtd(String qtd) {
        this.qtd = qtd;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Item: "+ produto + " Qtd: "+qtd;

//        return "Produto{" +
//                "id='" + id + '\'' +
//                ", produto='" + produto + '\'' +
//                ", qtd='" + qtd + '\'' +
//                '}';
    }
}
