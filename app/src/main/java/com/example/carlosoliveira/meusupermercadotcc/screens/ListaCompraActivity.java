package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Produto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListaCompraActivity extends AppCompatActivity {

    private RequestQueue mVolleyQueue;
    private ArrayList<Produto> produtos = new ArrayList<>();

    private ArrayList<Produto> prodEscolhidos = new ArrayList<>();

    @BindView(R.id.refresh)
    public SwipeRefreshLayout mRefresh;

    @BindView(R.id.listaprodutos)
    public AdapterView mListView;

    private static final int MENU_COMPRA = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compra);
        setTitle(getResources().getString(R.string.title_activity_lista_compra));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(ListaCompraActivity.this, AddProdutoActivity.class));
            }
        });

        mVolleyQueue = Volley.newRequestQueue(this);
        ButterKnife.bind(this);
        mRefresh.setEnabled(true);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lerProdutos();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Produto produto = ((ArrayAdapter<Produto>)parent.getAdapter()).getItem(position);
                prodEscolhidos.add(produto);
                //Toast.makeText(getBaseContext(), "Produto selecionado!", Toast.LENGTH_LONG).show();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Produto produto = ((ArrayAdapter<Produto>)parent.getAdapter()).getItem(position);
                Intent intent = new Intent(ListaCompraActivity.this, AddProdutoActivity.class);
                intent.putExtra("key", produto.getId() );
                intent.putExtra("produto", produto.getProduto() );
                intent.putExtra("qtd", produto.getQtd());
                intent.putExtra("idUser", produto.getIdUser());
                startActivity(intent);

                return true;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lerProdutos();
    }

    public void lerProdutos(){
        StringRequest req = new StringRequest("https://meusupermercadotcc.firebaseio.com/produto.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            jsonObject.toString();

                            Iterator<?> keys = jsonObject.keys();
                            produtos.clear();
                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( jsonObject.get(key) instanceof JSONObject ) {
                                    String produto = ((JSONObject) jsonObject.get(key)).get("produto").toString();
                                    String qtd = ((JSONObject) jsonObject.get(key)).get("qtd").toString();
                                    String idUser = ((JSONObject) jsonObject.get(key)).get("idUser").toString();
                                    Produto produto1 = new Produto(key, produto, qtd, idUser);
                                    if (idUser.equals(((Global) getApplication()).getIdUser())) {
                                        produtos.add(0,produto1);
                                    }
                                }
                            }
                            ArrayAdapter<Produto> adapter = new ArrayAdapter<Produto>(ListaCompraActivity.this,
                                    android.R.layout.simple_list_item_multiple_choice, produtos);
                            mListView.setAdapter(adapter);

                            mRefresh.setRefreshing(false);
                            //YoYo.with(Techniques.ZoomIn).playOn(mRefresh);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(ListaCompraActivity.this,"Erro",Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyQueue.add(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(MENU_COMPRA,MENU_COMPRA,10,"Comprar");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case MENU_COMPRA:
                if (prodEscolhidos.size()>0){
                    OrcamentoActivity.prodEscolhidos = prodEscolhidos;
                    Intent iorcamento = new Intent(ListaCompraActivity.this, OrcamentoActivity.class);
                    startActivity(iorcamento);
                    break;
                }else{
                    Toast.makeText(ListaCompraActivity.this,"Não há produtos selecionados para compra.",Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }
}
