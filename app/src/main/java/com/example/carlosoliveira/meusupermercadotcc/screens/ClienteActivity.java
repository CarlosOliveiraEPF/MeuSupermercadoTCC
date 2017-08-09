package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Cliente;

public class ClienteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        setTitle(getResources().getString(R.string.title_activity_cliente));
    }

    public void abrirPerfil(View view) {
        Intent iperfil = new Intent(ClienteActivity.this, PerfilActivity.class);
        startActivity(iperfil);
    }

    public void abrirLista(View view) {
        Intent ilistaProd = new Intent(ClienteActivity.this, ListaCompraActivity.class);
        startActivity(ilistaProd);
    }

    public void abrirOrcamento(View view) {
        Intent iorcamento = new Intent(ClienteActivity.this, OrcamentoActivity.class);
        startActivity(iorcamento);
    }

    public void getPedidos(View view) {
        Intent ipedidos = new Intent(ClienteActivity.this, PedidosActivity.class);
        startActivity(ipedidos);
    }
}