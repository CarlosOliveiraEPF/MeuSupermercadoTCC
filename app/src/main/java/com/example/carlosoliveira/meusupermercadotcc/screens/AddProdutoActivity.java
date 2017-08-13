package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Produto;

import org.json.JSONException;
import org.json.JSONObject;

public class AddProdutoActivity extends AppCompatActivity {

    private RequestQueue mVolleyRequest;
    private EditText mEdtDescProd;
    private EditText mEdtQtd;
    private Button mBtExcluir;
    private Button mBtSalvar;

    private Boolean inserePedido=false;
    private Boolean edit=false;
    private Produto produto;
    private EditText mEdtIdUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_produto);

        mVolleyRequest = Volley.newRequestQueue(this);
        mEdtDescProd = (EditText)findViewById(R.id.edtDescProduto);
        mEdtQtd = (EditText)findViewById(R.id.edtQtdProduto);
        mEdtIdUser = (EditText)findViewById(R.id.edtIdUser);

        mEdtIdUser.setText(((Global)getApplication()).getIdUser());

        mBtExcluir = (Button)findViewById(R.id.btnExcluir);
        mBtSalvar = (Button)findViewById(R.id.btnSalvar);
        //mBtExcluir.setVisibility(View.INVISIBLE);



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setVisibility(View.GONE);

        if (getIntent().hasExtra("key")){
            edit = true;
            produto = new Produto(getIntent().getStringExtra("key"),
                    getIntent().getStringExtra("produto" ),
                    getIntent().getStringExtra("qtd"),
                    getIntent().getStringExtra("iduser"));
            mEdtDescProd.setText(produto.getProduto());
            mEdtQtd.setText(produto.getQtd());
            mEdtIdUser.setText(produto.getIdUser());

            fab2.setVisibility(View.VISIBLE);
            //mBtExcluir.setVisibility(View.VISIBLE);
            //mBtSalvar.setVisibility(View.VISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarProduto(view);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirProduto(view);
            }
        });
    }

    public void salvarProduto(View view) {
        if (edit){
            editaProduto();
        }
        else {
            enviaProduto();
        }
    }
    public void excluirProduto(View view) {
        deletaProduto(view);
    }

    public void enviaProduto(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("produto", mEdtDescProd.getText().toString());
            obj.put("qtd", mEdtQtd.getText().toString());
            obj.put("idUser",mEdtIdUser.getText().toString());

            JsonObjectRequest json = new JsonObjectRequest(Request.Method.POST,
                    "https://meusupermercadotcc.firebaseio.com/produto.json", obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AddProdutoActivity.this, "Tente novamente.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            mVolleyRequest.add(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        /* Este trecho de código foi para a PedidosActivity.java
        if (! inserePedido) {
            try {
                Integer pedido;
                String status;
                pedido = 134827;
                status = "";

                for (int i=0; i < 6; i++){
                    if (i>0){
                        pedido += 37;
                    }
                    if ((i % 2)==0){
                        status = "Em produção";
                    }
                    if (((i % 2)==1)){
                        status = "Entrege";
                    }
                    if (((i % 3)==2)){
                        status = "Cancelado";
                    }

                    JSONObject obj2 = new JSONObject();
                    obj2.put("pedido", pedido.toString());
                    obj2.put("status",status.toString());
                    //obj2.put("data", mEdtQtd.getText().toString());

                    JsonObjectRequest json2 = new JsonObjectRequest(Request.Method.POST,
                            "https://meusupermercadotcc.firebaseio.com/pedido.json", obj2,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    finish();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(AddProdutoActivity.this, "Tente novamente.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    mVolleyRequest.add(json2);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            inserePedido = true;
        }
        */

    }

    public void editaProduto(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("produto", mEdtDescProd.getText().toString());
            obj.put("qtd", mEdtQtd.getText().toString());

            JsonObjectRequest json = new JsonObjectRequest(Request.Method.PATCH,
                    "https://meusupermercadotcc.firebaseio.com/produto/"+produto.getId()+".json", obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AddProdutoActivity.this, "Tente novamente.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            mVolleyRequest.add(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deletaProduto(View view){
        StringRequest json = new StringRequest(Request.Method.DELETE,
                "https://meusupermercadotcc.firebaseio.com/produto/"+produto.getId()+".json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddProdutoActivity.this, "Tente deletar novamente .",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        mVolleyRequest.add(json);
    }
}