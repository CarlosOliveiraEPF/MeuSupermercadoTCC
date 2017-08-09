package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carlosoliveira.meusupermercadotcc.BDados.ConfiguraFireBase;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Pedido;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PedidosActivity extends AppCompatActivity {

    private RequestQueue mVolleyRequest;
    private ArrayList<Pedido> pedidos = new ArrayList<>();

    private Pedido pedido;
    private DatabaseReference firebase;
    private FirebaseAuth mAuth;

    @BindView(R.id.refreshPedido)
    public SwipeRefreshLayout mRefresh;

    @BindView(R.id.listapedidos)
    public AdapterView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        setTitle(getResources().getString(R.string.title_activity_pedido));
        mAuth = FirebaseAuth.getInstance();

        firebase = ConfiguraFireBase.getFirebase().child("pedido");

        final ArrayList<Pedido> ped = new ArrayList<>();

        mVolleyRequest = Volley.newRequestQueue(this);
        ButterKnife.bind(this);
        mRefresh.setEnabled(true);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lerPedidos();
            };
        });

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ped.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Pedido p = data.getValue(Pedido.class);

                    p.setId(data.getKey()); //Colocando key manualmente no objeto
                    ped.add(p);
                }

                if(ped.isEmpty()) {
                    //Log.d("PEDIDOS", "PEDIDOS: " + ped.get(0).toString());
                    try {
                        Integer pedido;
                        String status;
                        pedido = 5557890;
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

                            JSONObject obj = new JSONObject();
                            obj.put("pedido", pedido.toString());
                            obj.put("status", status.toString());
                            obj.put("iduser", ((Global)getApplication()).getIdUser().toString());

                            JsonObjectRequest json = new JsonObjectRequest(Request.Method.POST,
                                    "https://meusupermercadotcc.firebaseio.com/pedido.json", obj,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            //finish();
                                            lerPedidos();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(PedidosActivity.this, "Tente novamente.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                            mVolleyRequest.add(json);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        lerPedidos();
    }

    public void lerPedidos(){
        StringRequest req = new StringRequest("https://meusupermercadotcc.firebaseio.com/pedido.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            jsonObject.toString();

                            Iterator<?> keys = jsonObject.keys();

                            pedidos.clear();

                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( jsonObject.get(key) instanceof JSONObject ) {
                                    String pedido = ((JSONObject) jsonObject.get(key)).get("pedido").toString();
                                    String status = ((JSONObject) jsonObject.get(key)).get("status").toString();
                                    String iduser = ((JSONObject) jsonObject.get(key)).get("iduser").toString();
                                    Pedido pedido1 = new Pedido(key, pedido, status, iduser);
                                    pedidos.add(0,pedido1);
                                }
                            }
                            ArrayAdapter<Pedido> adapter = new ArrayAdapter<Pedido>(PedidosActivity.this,
                                    android.R.layout.simple_list_item_1, pedidos);
                            mListView.setAdapter(adapter);
                            mRefresh.setRefreshing(false);
                            YoYo.with(Techniques.ZoomIn).playOn(mRefresh);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PedidosActivity.this,"Erro",Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyRequest.add(req);
    }
}