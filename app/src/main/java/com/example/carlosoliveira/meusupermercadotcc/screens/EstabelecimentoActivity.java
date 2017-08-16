package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.carlosoliveira.meusupermercadotcc.BDados.ConfiguraFireBase;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Estabelecimento;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EstabelecimentoActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    private static final String TAG = "logsGPS";

    private TextView tvLatitude;
    private TextView tvLongitude;
    //private ProgressBar progress;

    private double latitude;
    private double longitude;

    //Widgets
    private EditText etCEP;
    private ProgressBar progress;
    private EditText etLog;
    private EditText etName;
    private EditText etSite;
    private EditText etNumEst;
    private EditText etComplEst;

    private Estabelecimento estabelecimento;
    private DatabaseReference firebase;
    private Button btnSaveEst;
    //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estabelecimento);

        //Referencias
        tvLatitude = (TextView) findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) findViewById(R.id.tv_longitude);
        //progress = (ProgressBar) findViewById(R.id.progress);


        // setTitle(getResources().getString(R.string.title_activity_estabelecimento));
       // mAuth = FirebaseAuth.getInstance();

        etCEP = (EditText)findViewById(R.id.edtCepEst);
        etLog = (EditText)findViewById(R.id.edtLogEst);
        etName = (EditText)findViewById(R.id.edtNomeEst);
        etSite = (EditText)findViewById(R.id.edtSite);
        etNumEst = (EditText)findViewById(R.id.edtNumEst);
        etComplEst = (EditText)findViewById(R.id.edtCompEst);
        btnSaveEst = (Button)findViewById(R.id.btnSaveEst);

        progress = (ProgressBar)findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);

        firebase = ConfiguraFireBase.getFirebase().child("estabelecimento");

        final ArrayList<Estabelecimento> est = new ArrayList<>();

        //btnSaveEst.setBackgroundResource(R.drawable.if_cancel);

        etCEP.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus){
                    getCEP(etCEP.getText().toString());
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estabelecimento = new Estabelecimento();
                estabelecimento.setNome(etName.getText().toString());
                estabelecimento.setSite(etSite.getText().toString());
                estabelecimento.setCep(etCEP.getText().toString());
                estabelecimento.setLogradouro(etLog.getText().toString());
                estabelecimento.setNumero(etNumEst.getText().toString());
                estabelecimento.setComplemento(etComplEst.getText().toString());

                //Toast.makeText(getBaseContext(),estabelecimento.getLogradouro()+", "+estabelecimento.getNumero(), Toast.LENGTH_LONG).show();
                getLatLong(estabelecimento.getLogradouro()+", "+estabelecimento.getNumero());

                if(est.isEmpty()) {
                    salvarEstabelecimento(estabelecimento);
                    clearEstabelecimento();
                }else{
                    estabelecimento.setId(est.get(0).getId());
                    salvarEstabelecimentoAlterar(estabelecimento);
                    clearEstabelecimento();
                }
            }
        });

        btnSaveEst.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                estabelecimento = new Estabelecimento();
                estabelecimento.setNome(etName.getText().toString());
                estabelecimento.setSite(etSite.getText().toString());
                estabelecimento.setCep(etCEP.getText().toString());
                estabelecimento.setLogradouro(etLog.getText().toString());
                estabelecimento.setNumero(etNumEst.getText().toString());
                estabelecimento.setComplemento(etComplEst.getText().toString());
                if(est.isEmpty()) {
                    salvarEstabelecimento(estabelecimento);
                    clearEstabelecimento();
                }else{
                    estabelecimento.setId(est.get(0).getId());
                    salvarEstabelecimentoAlterar(estabelecimento);
                    clearEstabelecimento();
                }
            }
        });

        //Se não possui permissão -- Google
        if (ContextCompat.checkSelfPermission(EstabelecimentoActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Verifica se já mostramos o alerta e o usuário negou alguma vez.
            if (ActivityCompat.shouldShowRequestPermissionRationale(EstabelecimentoActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //Caso o usuário tenha negado a permissão anteriormente e não tenha marcado o check "nunca mais mostre este alerta"

                //Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                Toast.makeText(
                        getBaseContext(),
                        "Você já negou antes essa permissão! " +
                                "\nPara saber a sua localização necessitamos dessa permissão!",
                        Toast.LENGTH_LONG).show();

                        /* Além da mensagem indicando a necessidade sobre a permissão,
                           podemos solicitar novamente a permissão */
                ActivityCompat.requestPermissions(EstabelecimentoActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {
                //Solicita a permissão
                ActivityCompat.requestPermissions(EstabelecimentoActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }else {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            googleApiClient.connect();

        }// fecha Google



    }//fecha oncreate

    private boolean salvarEstabelecimentoAlterar (Estabelecimento estabelecimento) {
        try {
            //Alterando através da chave(key) no firebase setando o novo valor
            firebase.child(estabelecimento.getId()).setValue(estabelecimento);
            Toast.makeText(EstabelecimentoActivity.this, "Estabelecimento alterado com sucesso", Toast.LENGTH_LONG).show();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarEstabelecimento (Estabelecimento estabelecimento) {
        try {
            firebase.push().setValue(estabelecimento);
            Toast.makeText(EstabelecimentoActivity.this, "Estabelecimento inserido com sucesso", Toast.LENGTH_LONG).show();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Limpa os campos após salvar os dados do estabelecimento.
    public void clearEstabelecimento (){
        etCEP.setText("");
        etLog.setText("");
        etName.setText("");
        etSite.setText("");
        etNumEst.setText("");
        etComplEst.setText("");
        etName.requestFocus();
    }

    public void getCEP(String cep) {
        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://viacep.com.br/ws/" + cep + "/json/", params, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progress.setVisibility(View.VISIBLE);
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
                        etLog.setText(obj.getString("logradouro"));
                        if(etLog.length()<1){
                            Toast.makeText(getBaseContext(), "CEP não especifico de um logradouro. Favor preencher o campo."+statusCode, Toast.LENGTH_LONG).show();
                        }
                    }
                    progress.setVisibility(View.INVISIBLE);
                }catch(JSONException e){

                }
            }
        });
    }
    // Implementações Google
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(EstabelecimentoActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            Log.d(TAG, "Latitude Atual: " + lastLocation.getLatitude());
            Log.d(TAG, "Longitude Atual: " + lastLocation.getLongitude());

            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();

            //chamando o método getEndereco
            // Lat/Long Rua Ney da Gama Ahrends
            //latitude = -30.045504;
            //longitude = -51.1333411;


            getEndereco(latitude, longitude);

            tvLatitude.setText("Latitude: " + lastLocation.getLatitude());
            tvLongitude.setText("Longitude: " + lastLocation.getLongitude());

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
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getBaseContext(), "Problema na conexao!"+statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //Toast.makeText(getBaseContext(),responseString, Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
            }
        });
    }//fecha getEndereco

    public void getLatLong(String endereco) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://maps.googleapis.com/maps/api/geocode/json?address=" + endereco, params, new TextHttpResponseHandler() {
            //http://maps.googleapis.com/maps/api/geocode/json?address=Rua%20Alcides%20Foresti,%20461-507
            @Override
            public void onStart() {
                super.onStart();
                progress.setVisibility(View.VISIBLE);
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

                    Log.d("TAG","latitude do Endereço Estabelecimento: "+lati);
                    Log.d("TAG","longitude do Endereço Estabelecimento: "+longi);

                    latitude = Double.parseDouble(lati);
                    longitude = Double.parseDouble(longi);

//                    Toast.makeText(
//                            getBaseContext(),
//                            "Do endereço ... Latitude: " + latitude + "\nLongitude: " + longitude,
//                            Toast.LENGTH_LONG).show();

                    progress.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {

                }
                progress.setVisibility(View.GONE);
            }
        });
    }//fecha getlatLong



}//fecha EstabelecimentoActivity