package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Produto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.ButterKnife;

public class OrcamentoActivity extends AppCompatActivity {

    public static ArrayList<Produto> prodEscolhidos = new ArrayList<>();
    private ListView mListView;

    private static final int MENU_ORCAMENTO = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orcamento);
        setTitle(getResources().getString(R.string.title_activity_orcamento));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //BUSCANDO ARRAYLIST
        Log.d("logOrcamentoActivity",prodEscolhidos.toString());

        mListView = (ListView) findViewById(R.id.listaorcamento);
        ArrayAdapter<Produto> adapter = new ArrayAdapter<Produto>(OrcamentoActivity.this,
                android.R.layout.simple_list_item_1, prodEscolhidos);

        mListView.setAdapter(adapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//       //         Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//       //                 .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }//fecha oncreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(MENU_ORCAMENTO,MENU_ORCAMENTO,10,"Submeter Orçamento");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(OrcamentoActivity.this,"tamanho... "+prodEscolhidos.size(),Toast.LENGTH_SHORT).show();
        switch (item.getItemId()){
            case MENU_ORCAMENTO:
                if (prodEscolhidos.size()>0){
                    Intent isubmete = new Intent(OrcamentoActivity.this, ListaEstabelecimentoActivity.class);
                    startActivity(isubmete);
                    break;
                }else{
                    Toast.makeText(OrcamentoActivity.this,"Não há orçamentos para submeter.",Toast.LENGTH_SHORT).show();
                }

        }

        return super.onOptionsItemSelected(item);
    }

}//fecha classe