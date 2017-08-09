package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.app.Application;

/**
 * Created by carlos.oliveira on 06/08/2017.
 */

public class Global extends Application{

    private String idUser;
    private String emailuser;

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
}
