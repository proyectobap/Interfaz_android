package com.example.proyecto1.adds;

import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.R;


import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Contenedor_Adds extends AppCompatActivity implements RespuestaHilo {

/*La siguiente clase es un contenedor donde irán los fragmentos para añadir tickets, eventos y los
Elementos de hardware y software. Para ello vamos a necesitar los 4 fragmentos recogidos en variables
 */
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    final Fragment fragment1 = new newticket();
    final Fragment fragment2 = new newHardware();
    final Fragment fragment3 = new NewSoftware();
    final Fragment fragment4 = new NewEvent();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

/*En la creación de la ventana, la view será el layout activity_contenedor_adds, crearemos una toolbar
Para poder seleccionar nuestros fragmentos y haremos un listener para que la aplicación detecte
la selección en la barra. Por último, crearemos los 4 fragmentos en orden inverso y esconderemos los
tres primeros
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor__adds);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation3);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
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
                case R.id.add_ticket:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.add_hardware:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.add_software:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
                case R.id.add_event:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    return true;

            }
            return false;
        }
    };

    public void desconectar(MenuItem item){
        mapa.put("peticion","exit");
        JSONObject peticiones=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(peticiones,this);
    }


    @Override
    public void respuesta(JSONObject respuesta) {

    }

    public void newTicket(MenuItem item){

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = new Intent (this, newticket.class);
        startActivity(intent);

    }






}
