package com.example.proyecto1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.adds.Contenedor_Adds;
import com.example.proyecto1.lista.Elements;
import com.example.proyecto1.lista.Eventos;
import com.example.proyecto1.lista.ticket;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Contenedor_tickets extends AppCompatActivity implements RespuestaHilo {

    /*La siguiente clase es un contenedor donde irán los fragmentos para los detalles del ticket,
    los eventos y los elementos del ticket. Para ello vamos a necesitar los 3 fragmentos
    recogidos en variables
     */
    Elements e= new Elements();
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    final Fragment fragment1 = new ticket();
    final Fragment fragment2 = new Eventos();
    final Fragment fragment3 = new Elements();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    /*En la creación de la ventana, la view será el layout activity_contenedor_adds, crearemos una toolbar
    Para poder seleccionar nuestros fragmentos y haremos un listener para que la aplicación detecte
    la selección en la barra. Por último, crearemos los 3 fragmentos en orden inverso y esconderemos los
    dos primeros
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_tickets);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation2);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container_2, fragment3, "2").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container_2, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container_2, fragment1, "1").commit();


    }

    /*Esto es el listener de los botones de la barra de navegación inferior. Dependiendo de cuál
     se selecione, ocultará el activo y mostrará el indicado por el número
      */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.ticket:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;


                case R.id.eventos:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.elementos:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_2, menu);
        return super.onCreateOptionsMenu(menu);
    }



    public void conseguirID(JSONArray j) throws JSONException {
       String id =  j.getJSONObject(0).getString("id");
       mapa.clear();
       mapa.put("peticion", "assignelement");
       mapa.put("ticket_id", getIntent().getExtras().getString("id"));
       mapa.put("element_id", id);

    }

    public void peticion(JSONObject j){
        Informacion.getConexion().setInstruccion(j, this);
    }

    /*Estos métodos servirán para crear el menú superior de la pantalla. Tendrá dos botones, uno para
    ir al content que tiene las ventanas para crear tickets, eventos o
    elementos o para desconectar la aplicación
     */
    public void desconectar(MenuItem item){
        mapa.put("peticion","exit");
        JSONObject peticiones=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(peticiones, this);
        System.exit(0);
    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        Log.d("PRUEBA", "Estoy pasando por aqui");
        if (respuesta.getInt("response") == 200) {
            Log.e("ID", String.valueOf(ClienteTFG.contenido));
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("Etiquetas", content.toString());
            conseguirID(content);
        } else {

        }
    }

    public void newTicket(MenuItem item){

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = new Intent (this, Contenedor_Adds.class);
        startActivity(intent);

    }






}