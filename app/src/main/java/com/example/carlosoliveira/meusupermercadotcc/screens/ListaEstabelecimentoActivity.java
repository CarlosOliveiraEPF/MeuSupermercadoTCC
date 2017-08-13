package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Estabelecimento;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListaEstabelecimentoActivity extends AppCompatActivity {

    private RequestQueue mVolleyQueue;

    @BindView(R.id.refresh)
    public SwipeRefreshLayout mRefresh;

    @BindView(R.id.listaEstabelecimento)
    public AdapterView mListView;

    private ArrayList<Estabelecimento> estabelecimentos = new ArrayList<>();
    private ArrayList<Estabelecimento> estEscolhidos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_estabelecimento);
        setTitle("Submeter Orçamento");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Toast.makeText(getBaseContext(), "Orçamentos enviados para os estabelecimentos selecionados.", Toast.LENGTH_LONG).show();
            }
        });

        mVolleyQueue = Volley.newRequestQueue(this);
        ButterKnife.bind(this);
        mRefresh.setEnabled(true);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lerEstabelecimento();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Estabelecimento estabelecimento = ((ArrayAdapter<Estabelecimento>)parent.getAdapter()).getItem(position);
                estEscolhidos.add(estabelecimento);

                Toast.makeText(getBaseContext(), "Estabelecimento selecionado!", Toast.LENGTH_LONG).show();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lerEstabelecimento();
    }

    public void lerEstabelecimento(){
        StringRequest req = new StringRequest("https://meusupermercadotcc.firebaseio.com/estabelecimento.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            jsonObject.toString();

                            Iterator<?> keys = jsonObject.keys();
                            estabelecimentos.clear();
                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( jsonObject.get(key) instanceof JSONObject ) {
                                    String nome = ((JSONObject) jsonObject.get(key)).get("nome").toString();
                                    String logradouro = ((JSONObject) jsonObject.get(key)).get("logradouro").toString();
                                    String numero = ((JSONObject) jsonObject.get(key)).get("numero").toString();
                                    Estabelecimento estabelecimento1 = new Estabelecimento(key, nome, logradouro, numero);

                                    estabelecimentos.add(0,estabelecimento1);
                                }
                            }

                            ArrayAdapter<Estabelecimento> adapter = new ArrayAdapter<Estabelecimento>(ListaEstabelecimentoActivity.this,
                                    android.R.layout.simple_list_item_multiple_choice, estabelecimentos);
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
                        Toast.makeText(ListaEstabelecimentoActivity.this,"Erro",Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyQueue.add(req);
    }

}