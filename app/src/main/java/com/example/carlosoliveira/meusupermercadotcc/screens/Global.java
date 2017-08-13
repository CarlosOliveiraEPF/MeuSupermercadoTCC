package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.app.Application;

/**
 * Created by carlos.oliveira on 06/08/2017.
 */

public class Global extends Application{

    private String idUser="";
    private String emailuser="";
    private Boolean login=false;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getEmailuser() {
        return emailuser;
    }

    public void setEmailuser(String emailuser) {
        this.emailuser = emailuser;
    }

    public Boolean getLogin() {
        return login;
    }

    public void setLogin(Boolean login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
