package com.example.proyecto1;

import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Usuarios.Lista_usuarios;
import com.example.proyecto1.adds.newticket;
import com.example.proyecto1.lista.Lista_tickets;


import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Contenedor extends AppCompatActivity implements RespuestaHilo {


    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    final Fragment fragment1 = new Lista_tickets();
    final Fragment fragment2 = new Lista_usuarios();
    final Fragment fragment3 = new Own_User();
    Lista_usuarios l= new Lista_usuarios();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment3, "2").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.lista_tickets:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;


                case R.id.lista_usuarios:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.perfil:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void desconectar(MenuItem item){

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mapa.put("peticion","exit");
        JSONObject peticiones=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(peticiones,this);
        System.exit(0);
    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        Log.d("PRUEBA", "Estoy pasando por aqui");
        if (respuesta.getInt("response") == 200) {
            Log.e("PRUEBA", "good");

        } else {

        }
    }

    public void newTicket(MenuItem item){

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = new Intent (this, NewTicketAlone.class);
        startActivity(intent);

    }






}
