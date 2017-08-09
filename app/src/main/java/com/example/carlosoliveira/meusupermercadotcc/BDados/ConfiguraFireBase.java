package com.example.carlosoliveira.meusupermercadotcc.BDados;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by carlos.oliveira on 28/07/2017.
 */

public class ConfiguraFireBase {
    private static DatabaseReference referenceFireBase;
    private static FirebaseAuth autentication;

    public static DatabaseReference getFirebase() {
        if (referenceFireBase == null) {
            referenceFireBase = FirebaseDatabase.getInstance().getReference();
        }
        return referenceFireBase;
    }

    public static FirebaseAuth getFirebaseAutenticacao() {
        if (autentication == null) {
            autentication = FirebaseAuth.getInstance();
        }
        return  autentication;
    }
}
