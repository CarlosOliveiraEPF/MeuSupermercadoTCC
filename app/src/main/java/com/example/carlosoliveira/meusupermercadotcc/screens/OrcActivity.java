package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Pedido;
import com.example.carlosoliveira.meusupermercadotcc.classes.Produto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrcActivity extends AppCompatActivity {

    private RequestQueue mVolleyQueue;
    private ArrayList<Produto> produtos = new ArrayList<>();
    private ArrayList<Pedido> pedido = new ArrayList<>();

    @BindView(R.id.refreshOrc)
    public SwipeRefreshLayout mRefresh;

    @BindView(R.id.listProd)
    public AdapterView mListView1;


    //private ListView mListView1;
    private ListView mListView2;

    //private String [] data1 ={"Hiren", "Pratik", "Dhruv", "Narendra", "Piyush", "Priyank"};
    private String [] data2 ={"Kirit", "Miral", "Bhushan", "Jiten", "Ajay", "Kamlesh"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orc);

        //mListView1 = (ListView)findViewById(R.id.listProd);
        mListView2 = (ListView)findViewById(R.id.listEst);

        //mListView1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data1));
        mListView2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data2));

        //ListUtils.setDynamicHeight(mListView1);
        ListUtils.setDynamicHeight(mListView2);


        mVolleyQueue = Volley.newRequestQueue(this);
        ButterKnife.bind(this);
        mRefresh.setEnabled(true);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lerProdutos2();
            }
        });


        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produto produto = ((ArrayAdapter<Produto>)parent.getAdapter()).getItem(position);
                Intent intent = new Intent(OrcActivity.this, null);
                intent.putExtra("key", produto.getId() );
                intent.putExtra("produto", produto.getProduto() );
                intent.putExtra("qtd", produto.getQtd());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        lerProdutos2();
    }
    public void lerProdutos2(){
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
                                    String iduser = ((JSONObject) jsonObject.get(key)).get("iduser").toString();
                                    Produto produto1 = new Produto(key, produto, qtd, iduser);
                                    produtos.add(0,produto1);
                                }
                            }
                            ArrayAdapter<Produto> adapter = new ArrayAdapter<Produto>(OrcActivity.this,
                                    android.R.layout.simple_list_item_1, produtos);
                            mListView1.setAdapter(adapter);
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
                        Toast.makeText(OrcActivity.this,"Erro",Toast.LENGTH_SHORT).show();
                    }
                });
        mVolleyQueue.add(req);
    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}