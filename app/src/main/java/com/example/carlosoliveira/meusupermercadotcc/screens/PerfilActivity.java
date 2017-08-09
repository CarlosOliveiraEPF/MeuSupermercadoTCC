package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carlosoliveira.meusupermercadotcc.BDados.ConfiguraFireBase;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Cliente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PerfilActivity extends AppCompatActivity {
    private ProgressBar progressCli;
    private EditText etCEPCli;
    private EditText etLogCli;
    private EditText etNameCli;
    private EditText etEmail;
    private EditText etNumCli;
    private EditText etComplCli;

    private Cliente cliente;
    private DatabaseReference firebase;
    public EditText etIdUser;
    //private Button btnSaveEst;
   // private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        setTitle(getResources().getString(R.string.title_activity_perfil));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //mAuth = FirebaseAuth.getInstance();

        progressCli = (ProgressBar)findViewById(R.id.progressC);
        progressCli.setVisibility(View.INVISIBLE);

        etCEPCli = (EditText)findViewById(R.id.edtCepCli);
        etLogCli = (EditText)findViewById(R.id.edtLogCli);
        etNameCli = (EditText)findViewById(R.id.edtNomeCli);
        etEmail = (EditText)findViewById(R.id.edtEmail);
        etNumCli = (EditText)findViewById(R.id.edtNumCli);
        etComplCli = (EditText)findViewById(R.id.edtComplCli);
        etIdUser = (EditText) findViewById(R.id.edtIdUser);

        firebase = ConfiguraFireBase.getFirebase().child("cliente");

        final ArrayList<Cliente> cli = new ArrayList<>();

        etCEPCli.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus){
                    getCEPCliente(etCEPCli.getText().toString());
                    //Toast.makeText(getBaseContext(),"Saiu do campo CEP",Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                cliente = new Cliente();
                cliente.setNome(etNameCli.getText().toString());
                cliente.setEmail(etEmail.getText().toString());
                cliente.setCep(etCEPCli.getText().toString());
                cliente.setLogradouro(etLogCli.getText().toString());
                cliente.setNumero(etNumCli.getText().toString());
                cliente.setComplemento(etComplCli.getText().toString());
                if(cli.isEmpty()) {
                    salvarCliente(cliente);
                }else{
                    cliente.setId(cli.get(0).getId());
                    salvarClienteAlterar(cliente);
                }

            }
        });




        //filtro - Está sendo visto com o Thiago. Não funcionando.
        firebase.orderByChild("email").equalTo(((Global)getApplication()).getEmailuser().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cli.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Cliente c = data.getValue(Cliente.class);

                    c.setId(data.getKey()); //Colocando key manualmente no objeto
                    cli.add(c);
                }
                if(!cli.isEmpty()) {
                    Log.d("CLIENTES", "CLIENTES: " + cli.get(0).toString());
                    etCEPCli.setText(cli.get(0).getCep());
                    etNameCli.setText(cli.get(0).getNome());
                    etEmail.setText(cli.get(0).getEmail());
                    etLogCli.setText(cli.get(0).getLogradouro());
                    etNumCli.setText(cli.get(0).getNumero());
                    etComplCli.setText(cli.get(0).getComplemento());

                    Intent intent = new Intent(PerfilActivity.this, ListaCompraActivity.class);
                    etIdUser.setText(cli.get(0).getId());
                    ((Global) getApplication()).setIdUser(etIdUser.getText().toString());
                }else{
                    Toast.makeText(getBaseContext(),"Cliente não possui perfil cadastrado.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
        // Código do Thiago
        firebase.orderByChild("email").startAt("raul@gmail.com").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        cli.clear();
                        Log.d("logPerfilActivity", "Entrou no PerfilActivity");
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            Log.d("logPerfilActivity", "Entrou no for");
                            Log.d("logPerfilActivity", "o que ele buscou: "+data.toString());
                            Cliente c = data.getValue(Cliente.class);
                            cli.add(c);
                        }
                        Log.d("logPerfilActivity","teste retorno: "+cli.toString());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
*/
        //buscar os dados do cliente (perfil)
/*        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cli.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Cliente c = data.getValue(Cliente.class);

                    c.setId(data.getKey()); //Colocando key manualmente no objeto
                    cli.add(c);
                }

                if(!cli.isEmpty()) {

                    Log.d("CLIENTES", "CLIENTES: " + cli.get(0).toString());
                    etCEPCli.setText(cli.get(0).getCep());
                    etNameCli.setText(cli.get(0).getNome());
                    etEmail.setText(cli.get(0).getEmail());
                    etLogCli.setText(cli.get(0).getLogradouro());
                    etNumCli.setText(cli.get(0).getNumero());
                    etComplCli.setText(cli.get(0).getComplemento());

                    Intent intent = new Intent(PerfilActivity.this, ListaCompraActivity.class);
                    etIdUser.setText(cli.get(0).getId());

                    //Global global = new Global();
                    ((Global)getApplication()).setIdUser(etIdUser.getText().toString());
                    //global.setIdUser(etIdUser.getText().toString());
                    Toast.makeText(getBaseContext(),"User ID XXX "+((Global)getApplication()).getIdUser(),Toast.LENGTH_LONG).show();

                    //edtTexto = (TextView) findViewById(R.id.edtTexto);

                    String txt = "";
                    txt = etIdUser.getText().toString();
                    Bundle bundle = new Bundle();

                    bundle.putString("txt", txt);
                    intent.putExtras(bundle);
                    Toast.makeText(getBaseContext(),"User ID YYY "+txt,Toast.LENGTH_LONG).show();
                    //startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
*/
    }//FECHA ONCREATE

    private boolean salvarClienteAlterar (Cliente cliente) {
        try {
            //Alterando através da chave(key) no firebase setando o novo valor
            firebase.child(cliente.getId()).setValue(cliente);
            Toast.makeText(PerfilActivity.this, "Perfil alterado com sucesso", Toast.LENGTH_LONG).show();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarCliente (Cliente cliente) {
        try {
            firebase.push().setValue(cliente);
            Toast.makeText(PerfilActivity.this, "Perfil inserido com sucesso", Toast.LENGTH_LONG).show();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void getCEPCliente(String cep) {
        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://viacep.com.br/ws/" + cep + "/json/", params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressCli.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getBaseContext(), "Problema na conexao!"+statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject obj = new JSONObject(responseString);

                    String retorno = "";

                    if (!obj.has("erro")) {
                         etLogCli.setText(obj.getString("logradouro"));
                    }
                    progressCli.setVisibility(View.INVISIBLE);
                }catch(JSONException e){

                }
            }
        });
    }
}