package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.carlosoliveira.meusupermercadotcc.BDados.ConfiguraFireBase;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Cliente;
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

    final ArrayList<Cliente> clinew = new ArrayList<>();
    public Boolean emailExistente=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        setTitle(getResources().getString(R.string.title_activity_perfil));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 cliente = new Cliente();
                cliente.setNome(etNameCli.getText().toString());
                cliente.setEmail(etEmail.getText().toString());
                cliente.setCep(etCEPCli.getText().toString());
                cliente.setLogradouro(etLogCli.getText().toString());
                cliente.setNumero(etNumCli.getText().toString());
                cliente.setComplemento(etComplCli.getText().toString());
                ((Global)getApplication()).setEmailuser(cliente.getEmail());

                if (cli.isEmpty()) {
                    salvarCliente(cliente);
                } else {
                    cliente.setId(cli.get(0).getId());
                    salvarClienteAlterar(cliente);
                }
            }
        });
        // Resgata os daos do usuário logado
        if (((Global) getApplication()).getLogin()) {
            firebase.orderByChild("email").equalTo(((Global)getApplication()).getEmailuser()).addValueEventListener(new ValueEventListener() {
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
                        etIdUser.setText(cli.get(0).getId());
                        ((Global) getApplication()).setIdUser(etIdUser.getText().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }//FECHA ONCREATE

    // Salva cliente em caso de novo cadastro
    private boolean salvarCliente (Cliente cliente) {
        try {
            firebase.push().setValue(cliente);
            //((Global)getApplication()).setIdUser(cliente.getId());
            Toast.makeText(PerfilActivity.this, "Perfil inserido com sucesso. Efetuar login para acesso ao App.", Toast.LENGTH_LONG).show();
            Intent imain = new Intent(PerfilActivity.this, MainActivity.class);
            startActivity(imain);
            return true;
        } catch (Exception e) {
            Toast.makeText(PerfilActivity.this, "Erro: "+e, Toast.LENGTH_LONG).show();
            return false;
        }
    }//Final salvarCliente

    // Salva Cliente em caso de edição do cadastro
    private boolean salvarClienteAlterar (Cliente cliente) {
        try {
         //Alterando através da chave(key) no firebase setando o novo valor
            if (existeEmail(cliente)){
                firebase.child(cliente.getId()).setValue(cliente);
                ((Global)getApplication()).setEmailuser(cliente.getEmail());
                ((Global)getApplication()).setIdUser(cliente.getId());
                Toast.makeText(PerfilActivity.this, "Perfil alterado com sucesso", Toast.LENGTH_LONG).show();
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }//Final salvarClienteAlterar

    // Usa API para fazer a busca pelo CEP informado no cadastro do usuário, resgatando a rua.
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
    }// Final getCEPCliente

    // Verifica a existência de um usuário já cadastrado com o mesmo e-mail.
    private boolean existeEmail(Cliente cliente) {
        try {
            firebase.orderByChild("email").equalTo(((Global)getApplication()).getEmailuser()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    clinew.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Cliente c = data.getValue(Cliente.class);
                        c.setId(data.getKey()); //Colocando key manualmente no objeto
                        clinew.add(c);
                    }
                    if (!clinew.isEmpty()) {
                        if (!clinew.get(0).getId().equals(((Global) getApplication()).getIdUser())) {
                            Toast.makeText(getBaseContext(), "Email informado já cadastrado para outro usuário. Não foi possível salvar o cadastro.", Toast.LENGTH_LONG).show();
                        } else {
                            emailExistente = true;
                        }
                    } else {
                        emailExistente = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return emailExistente;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }// Final existeEmail

}// Final PerfilActivity