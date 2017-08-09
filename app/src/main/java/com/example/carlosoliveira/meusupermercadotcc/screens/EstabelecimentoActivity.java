package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estabelecimento);

        //Referencias
        tvLatitude = (TextView) findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) findViewById(R.id.tv_longitude);
        //progress = (ProgressBar) findViewById(R.id.progress);


        // setTitle(getResources().getString(R.string.title_activity_estabelecimento));
        mAuth = FirebaseAuth.getInstance();

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
                        }else{
                            Toast.makeText(getBaseContext(), "Logradouro: "+etLog.getText().toString(), Toast.LENGTH_LONG).show();
                            getEndereco(0,0,etLog.getText().toString());
                        }
//                        retorno += "\n" + obj.getString("cep");
//                        retorno += "\n" + obj.getString("logradouro");
//                        retorno += "\n" + obj.getString("complemento");
//                        retorno += "\n" + obj.getString("bairro");
//                        retorno += "\n" + obj.getString("localidade");
//                        retorno += "\n" + obj.getString("uf");
//                        retorno += "\n" + obj.getString("ibge");
//                        retorno += "\n" + obj.getString("gia");
//
//                        Toast.makeText(getBaseContext(),"Dados retornados: "+retorno, Toast.LENGTH_LONG).show();
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

            Log.d(TAG, "Latitude: " + lastLocation.getLatitude());
            Log.d(TAG, "Longitude: " + lastLocation.getLongitude());

            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();

            //chamando o método getEndereco
            // Lat/Long Rua Ney da Gama Ahrends
            //latitude = -30.045504;
            //longitude = -51.1333411;
            getEndereco(latitude, longitude, etLog.getText().toString());

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

    public void getEndereco(double lat, double longi, String logradouro){
        //http://maps.googleapis.com/maps/api/geocode/json?latlng=-26.196223,-52.689523
        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        //if(logradouro.isEmpty()){

        //}
        client.get("http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+longi, params, new TextHttpResponseHandler() {
        //client.get("http://maps.google.com/maps/api/geocode/json?address="+logradouro, params, new TextHttpResponseHandler() {
                    //http://maps.google.com/maps/api/geocode/json?address=rua+ney+gama+ahrends+295+prot%C3%A1sio,+portoalegre+-+rs&sensor=false


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

                //aqui que retorna a lat e long.
                //Toast.makeText(getBaseContext(), "Logradouro: "+etLog.getText().toString(), Toast.LENGTH_LONG).show();


                try {
                    JSONObject objGps = new JSONObject(responseString);

                    String retorno = "";
                    retorno = objGps.getString("short_name");
                    Toast.makeText(getBaseContext(),retorno, Toast.LENGTH_LONG).show();
//
//                    if (!obj.has("erro")) {
//                        etLog.setText(obj.getString("logradouro"));
//                        if(etLog.length()<1){
//                            Toast.makeText(getBaseContext(), "CEP não especifico de um logradouro. Favor preencher o campo."+statusCode, Toast.LENGTH_LONG).show();
//                        }else{
//                            Toast.makeText(getBaseContext(), "Logradouro: "+etLog.getText().toString(), Toast.LENGTH_LONG).show();
//                            getEndereco(0,0,etLog.getText().toString());
//                        }
//                    }
//                    progress.setVisibility(View.INVISIBLE);
                }catch(JSONException e){

                }





                Toast.makeText(getBaseContext(),responseString, Toast.LENGTH_LONG).show();

                progress.setVisibility(View.GONE);
            }
        });
    }//fecha Implementações google


}//fecha EstabelecimentoActivity