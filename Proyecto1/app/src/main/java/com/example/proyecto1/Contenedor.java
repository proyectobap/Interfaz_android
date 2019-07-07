package com.example.proyecto1;

import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Usuarios.Lista_usuarios;
import com.example.proyecto1.Usuarios.Own_User;
import com.example.proyecto1.lista.Lista_tickets;
import com.example.proyecto1.lista.NewTicketAlone;


import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Contenedor extends AppCompatActivity implements RespuestaHilo {

    /*La siguiente clase es un contenedor donde irán los fragmentos para las listas de tickets, usuarios
    y el propio usuario. Para ello vamos a necesitar los 3 fragmentos recogidos en variables
     */
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    final Fragment fragment1 = new Lista_tickets();
    final Fragment fragment2 = new Lista_usuarios();
    final Fragment fragment3 = new Own_User();
    Lista_usuarios l= new Lista_usuarios();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    Intent intent;


    /*En la creación de la ventana, la view será el layout activity_contenedor_adds, crearemos una toolbar
    Para poder seleccionar nuestros fragmentos y haremos un listener para que la aplicación detecte
    la selección en la barra. Por último, crearemos los 3 fragmentos en orden inverso y esconderemos los
    dos primeros
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent= new Intent(this, New_user.class);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment3, "2").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();


    }

    /*Esto es el listener de los botones de la barra de navegación inferior. Dependiendo de cuál
  se selecione, ocultará el activo y mostrará el indicado por el número
   */
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


/*Estos métodos servirán para crear el menú superior de la pantalla. Tendrá dos botones, uno para
ir a la activity de crear un ticket nuevo (sin haber seleccionado uno antes de la lista) y otro
para crear un usuario (si no eres un cliente)
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void new_user(MenuItem item){
        if(!Loading.own_type.equals("2")){
            startActivity(intent);
        } else{
            Toast toast = Toast.makeText(this, "Los clientes no pueden añadir usuarios",
                    Toast.LENGTH_LONG);
            toast.show();
        }

    }

    @Override
    public void respuesta(JSONObject respuesta) {
    }


    @Override
    public void onBackPressed(){
        mapa.put("peticion","exit");
        JSONObject peticiones=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(peticiones,this);
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }

    public void newTicket(MenuItem item){

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = new Intent (this, NewTicketAlone.class);
        startActivity(intent);

    }






}
