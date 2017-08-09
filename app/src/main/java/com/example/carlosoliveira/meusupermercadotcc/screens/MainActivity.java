package com.example.carlosoliveira.meusupermercadotcc.screens;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carlosoliveira.meusupermercadotcc.R;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {

    //Drawer
    private Drawer result = null;

    //Toolbar
    private Toolbar toolbar;
    private Button btnLogin;
    private EditText etEmailUser;
    private Boolean ligaFab=false;
    private String idUser;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabLogin);
        //fab.setVisibility(View.INVISIBLE);

//        if (ligaFab){
//            Toast.makeText(getBaseContext(),"liga..."+etEmailUser.length(),Toast.LENGTH_LONG).show();
//            fab.setVisibility(View.VISIBLE);
//        }

        etEmailUser = (EditText)findViewById(R.id.edtUserEmail);
//        etEmailUser.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//            @Override
//            public void onFocusChange(View v, boolean hasFocus)
//            {
//                if ((!hasFocus)&&(etEmailUser.length()>0)){
//                    ligaFab = true;
//                    Toast.makeText(getBaseContext(),"Email Usuário preenchido..."+etEmailUser.length(),Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Email Usuário preenchido..."+etEmailUser.getText().toString(),Toast.LENGTH_LONG).show();
                ((Global)getApplication()).setEmailuser(etEmailUser.getText().toString());
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });



        //Inicio AccountHeader
        //####################### SÓ O CABEÇALHO #######################
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.meusuper1)
                .addProfiles(
                        //new ProfileDrawerItem().withName("Thiago Cury").withEmail("thiagocury@gmail.com").withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
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
                        new PrimaryDrawerItem().withName("Estabelecimento").withIdentifier(1).withIcon(R.mipmap.ic_launcher_mall)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch ((int)drawerItem.getIdentifier()){
                            case 0:
                                Intent icliente = new Intent(MainActivity.this, ClienteActivity.class);
                                startActivity(icliente);
                                break;
                            case 1:
                                Intent iestabelecimento = new Intent(MainActivity.this, EstabelecimentoActivity.class);
                                startActivity(iestabelecimento);
                                break;
                        }
                        return false;
                    }
                }).build();
    }
}
