package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carlosoliveira.meusupermercadotcc.BDados.ConfiguraFireBase;
import com.example.carlosoliveira.meusupermercadotcc.R;
import com.example.carlosoliveira.meusupermercadotcc.classes.Cliente;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Drawer
    private Drawer result = null;

    //Toolbar
    private Toolbar toolbar;
    private Button btnLogin;
    private EditText etEmailUser;
    private Boolean ligaFab=false;
    private String idUser;

    private DatabaseReference firebase;
    final ArrayList<Cliente> cli = new ArrayList<>();

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebase = ConfiguraFireBase.getFirebase().child("cliente");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabLogin);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fabLogoff);
        fab2.setVisibility(View.GONE);// INVISIBLE);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Trestando a existência do e-mail na base de dados.
                etEmailUser = (EditText)findViewById(R.id.edtUserEmail);
                firebase.orderByChild("email").equalTo(etEmailUser.getText().toString()).addValueEventListener(new ValueEventListener() {
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
                            ((Global)getApplication()).setEmailuser(etEmailUser.getText().toString());
                            ((Global) getApplication()).setIdUser(cli.get(0).getId());
                            Intent icliente = new Intent(MainActivity.this, ClienteActivity.class);
                            startActivity(icliente);
                            //fab.setVisibility(View.GONE);
                            //fab2.setVisibility(View.VISIBLE);
                            ((Global) getApplication()).setLogin(true);

                        }else{
                            etEmailUser.setText("");
                            Toast.makeText(getBaseContext(),"Usuário não cadastrado. Informe seu email cadastrado ou acesse o menu para efetuar seu cadastro.",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etEmailUser.setText("");
                ((Global)getApplication()).setEmailuser(etEmailUser.getText().toString());
                ((Global)getApplication()).setIdUser(etEmailUser.getText().toString());

                Toast.makeText(getBaseContext(),"Você está desconectado.",Toast.LENGTH_LONG).show();
                fab2.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                ((Global) getApplication()).setLogin(false);
            }

        });

            //Inicio AccountHeader
        //####################### SÓ O CABEÇALHO #######################
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.fundo_menu)
                .addProfiles(
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener(){
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //Menu
        result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        //SecondaryDrawerItem()
                        new PrimaryDrawerItem().withName("Cliente").withIdentifier(0).withIcon(R.mipmap.ic_launcher_person),
                        new PrimaryDrawerItem().withName("Estabelecimento").withIdentifier(1).withIcon(R.mipmap.ic_launcher_mall),
                        new PrimaryDrawerItem().withName("Sobre").withIdentifier(2).withIcon(R.mipmap.ic_info),
                        new PrimaryDrawerItem().withName("Sair").withIdentifier(3).withIcon(R.mipmap.ic_exit)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int)drawerItem.getIdentifier()){
                            case 0:
                                if (!((Global) getApplication()).getLogin()) {
                                    Intent icliente = new Intent(MainActivity.this, ClienteActivity.class);
                                    startActivity(icliente);
                                    break;
                                }else{
                                    Toast.makeText(getBaseContext(),"Área de acesso para novos Clientes/Estabelecimento.",Toast.LENGTH_LONG).show();
                                    break;
                                }
                            case 1:
                                if (!((Global) getApplication()).getLogin()) {
                                    Intent iestabelecimento = new Intent(MainActivity.this, EstabelecimentoActivity.class);
                                    startActivity(iestabelecimento);
                                    break;
                                }else{
                                    Toast.makeText(getBaseContext(),"Área de acesso para novos Clientes/Estabelecimento.",Toast.LENGTH_LONG).show();
                                    break;
                                }

                            case 2:
                                    Intent isobre = new Intent(MainActivity.this, SobreActivity.class);
                                    startActivity(isobre);
                                    break;

                            case 3:
                                if (((Global) getApplication()).getLogin()) {
                                    etEmailUser.setText("");
                                    fab.setVisibility(View.VISIBLE);
                                    ((Global)getApplication()).setEmailuser(etEmailUser.getText().toString());
                                    ((Global)getApplication()).setIdUser(etEmailUser.getText().toString());
                                    ((Global) getApplication()).setLogin(false);
                                    Toast.makeText(getBaseContext(),"Você está desconectado.",Toast.LENGTH_LONG).show();
                                    break;

                                }else{
                                    Toast.makeText(getBaseContext(),"Usuário não efetuou login.",Toast.LENGTH_LONG).show();
                                    break;
                                }
                        }
                     return false;

                    }
                }).build();
    }
}