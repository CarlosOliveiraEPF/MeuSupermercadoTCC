package com.example.carlosoliveira.meusupermercadotcc.classes;

/**
 * Created by carlos.oliveira on 31/07/2017.
 */

public class Pedido {
    private String id;
    private String pedido;
    private String status;
    private String idUser;

    public Pedido(String id, String pedido, String status, String idUser ) {
        this.id = id;
        this.pedido = pedido;
        this.status = status;
        this.idUser = idUser;
    }

    public Pedido() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Pedido: "+ pedido + " Status:  "+status;
    }
}
