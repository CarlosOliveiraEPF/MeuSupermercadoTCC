package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

public class PerfilActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ProgressBar progressCli;
    private EditText etCEPCli;
    private EditText etLogCli;
    private EditText etNameCli;
    private EditText etEmail;
    private EditText etNumCli;
    private EditText etComplCli;
    public EditText etIdUser;
    private EditText etLatCli;
    private EditText etLongCli;

    private Cliente cliente;
    private DatabaseReference firebase;

    final ArrayList<Cliente> clinew = new ArrayList<>();
    public Boolean emailExistente=false;

    private GoogleApiClient googleApiClient;
    private static final String TAG = "logsGPS";

    private double latitude;
    private double longitude;

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
        etLatCli = (EditText) findViewById(R.id.edtLatCli);
        etLongCli = (EditText) findViewById(R.id.edtLongCli);

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
                cliente.setLatitude(etLatCli.getText().toString());
                cliente.setLongitude(etLongCli.getText().toString());
                ((Global)getApplication()).setEmailuser(cliente.getEmail());

                if (cli.isEmpty()) {
                    //Toast.makeText(PerfilActivity.this, "Endereço: "+cliente.getLogradouro()+", "+cliente.getNumero(), Toast.LENGTH_LONG).show();
                    getLatLong(cliente.getLogradouro()+", "+cliente.getNumero());
                    if (cliente.getEmail().isEmpty()){
                        Toast.makeText(PerfilActivity.this, "Campo e-mail obrigatório..", Toast.LENGTH_LONG).show();
                        etEmail.requestFocus();
                    }else{
                        salvarCliente(cliente);
                    }
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
                        etLatCli.setText(cli.get(0).getLatitude());
                        etLongCli.setText(cli.get(0).getLongitude());

                        ((Global) getApplication()).setIdUser(etIdUser.getText().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //Se não possui permissão -- Google
        if (ContextCompat.checkSelfPermission(PerfilActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Verifica se já mostramos o alerta e o usuário negou alguma vez.
            if (ActivityCompat.shouldShowRequestPermissionRationale(PerfilActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //Caso o usuário tenha negado a permissão anteriormente e não tenha marcado o check "nunca mais mostre este alerta"

                //Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                Toast.makeText(
                        getBaseContext(),
                        "Você já negou antes essa permissão! " +
                                "\nPara saber a sua localização necessitamos dessa permissão!",
                        Toast.LENGTH_LONG).show();

                        /* Além da mensagem indicando a necessidade sobre a permissão,
                           podemos solicitar novamente a permissão */
                ActivityCompat.requestPermissions(PerfilActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {
                //Solicita a permissão
                ActivityCompat.requestPermissions(PerfilActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }else {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            googleApiClient.connect();

        }// fecha Google

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


    // Implementações Google
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(PerfilActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            Log.d(TAG, "Latitude: " + lastLocation.getLatitude());
            Log.d(TAG, "Longitude: " + lastLocation.getLongitude());

            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();

            //chamando o método getEndereco
            // Lat/Long Rua Ney da Gama Ahrends
            //latitude = -30.045504;
            //longitude = -51.1333411;


            getEndereco(latitude, longitude);

//            Toast.makeText(
//                    getBaseContext(),
//                    "Passou aqui...",
//                    Toast.LENGTH_LONG).show();

            if (etLogCli.getText().toString().length()<1){
                etLatCli.setText("Latitude x: " + lastLocation.getLatitude());
                etLongCli.setText("Longitude x: " + lastLocation.getLongitude());
            }


        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(
                getBaseContext(),
                "Conexão falhou!",
                Toast.LENGTH_LONG).show();
    }

    public void getEndereco(double lat, double longi){
        //http://maps.googleapis.com/maps/api/geocode/json?latlng=-26.196223,-52.689523
        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+longi, params, new TextHttpResponseHandler() {
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
                // Retorna o JSON que captura a posição da Latitude/Longitude.
                //Toast.makeText(getBaseContext(),responseString, Toast.LENGTH_LONG).show();
                progressCli.setVisibility(View.GONE);
            }
        });
    }//fecha getEndereco

    public void getLatLong(String endereco) {
        String end = cliente.getLogradouro()+", "+cliente.getNumero();
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://maps.googleapis.com/maps/api/geocode/json?address=" + end, params, new TextHttpResponseHandler() {
            //http://maps.googleapis.com/maps/api/geocode/json?address=Rua%20Alcides%20Foresti,%20461-507
            @Override
            public void onStart() {
                super.onStart();
                progressCli.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getBaseContext(), "Problema na conexao!" + statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {

                    JSONObject obj = new JSONObject(responseString);

                    String resp = obj.toString();

                    Log.d("TAG","puro: "+resp);

                    String lati = resp.substring(resp.indexOf("location") + 17, resp.indexOf("location") + 27);
                    String longi = resp.substring(resp.indexOf("location") + 35, resp.indexOf("location") + 45);

                    Log.d("TAG","latitude: "+lati);
                    Log.d("TAG","longitude: "+longi);

                    latitude = Double.parseDouble(lati);
                    longitude = Double.parseDouble(longi);

//                    Toast.makeText(
//                            getBaseContext(),
//                            "Do endereço ... Latitude: " + latitude + "\nLongitude: " + longitude,
//                            Toast.LENGTH_LONG).show();
                    etLatCli.setText(lati);
                    etLongCli.setText(longi);
                    cliente.setLatitude(lati);
                    cliente.setLongitude(longi);

                    progressCli.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {

                }
                progressCli.setVisibility(View.GONE);
            }
        });
    }//fecha getlatLong










}// Final PerfilActivity