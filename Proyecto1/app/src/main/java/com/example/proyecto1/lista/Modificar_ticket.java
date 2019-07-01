package com.example.proyecto1.lista;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Contenedor_tickets;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class Modificar_ticket extends AppCompatActivity implements RespuestaHilo {
    TextInputEditText titulo;
    TextInputEditText des;
    TextInputEditText estad;
    TextInputEditText ticket_owner;
    TextInputEditText ticket_object;
    Contenedor_tickets c;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    public static String id_ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_ticket);
        c= new Contenedor_tickets();
        id_ticket = this.getIntent().getExtras().getString("id");
        Bundle s = this.getIntent().getExtras();
        initializeUI();

    }

    private void initializeUI() {

        Bundle s = this.getIntent().getExtras();
        String title= s.getString("titulo");
        String descripcion= s.getString("descripcion");
        String estado = estado(s.getString("estado"));
        String owner = s.getString("ticket_owner");
        String object = s.getString("ticket_object");


        titulo = (TextInputEditText) findViewById(R.id.title);

        titulo.setText(title);

        des = (TextInputEditText) findViewById(R.id.desc);

        des.setText(descripcion);

        ticket_owner = (TextInputEditText) findViewById(R.id.owner);

        ticket_owner.setText(owner);

        ticket_object = (TextInputEditText) findViewById(R.id.object);

        ticket_object.setText(object);

        estad = (TextInputEditText) findViewById(R.id.estado);

        estad.setText(estado);



    }

    public void cambiarTicket(View v){
        mapa.clear();
        mapa.put("peticion", "modifyticket");
        mapa.put("title", titulo.getText().toString());
        mapa.put("desc", des.getText().toString());
        mapa.put("ticket_status_id", intestado(estad.getText().toString()));
        mapa.put("ticket_owner", ticket_owner.getText().toString());
        mapa.put("ticket_object", ticket_object.getText().toString());
        mapa.put("ticket_id", id_ticket);
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }

    public String intestado(String s){
        switch (s) {
            case "Abierto":
                s = "1";
                return s;


            case "Asignado":
                s = "2";
                return s;

            case "En espera":
                s = "3";
                return s;

            case "":
                s = "4";
                return s;

            case "Solucionado":
                s = "5";
                return s;

            case "6":
                s = "Cerrado";
                return s;

        }
        return s;
    }
    public String estado(String s){
        switch (s) {
            case "1":
                s = "Abierto";
                return s;


            case "2":
                s = "Asignado";
                return s;

            case "3":
                s = "En espera";
                return s;

            case "4":
                s = "";
                return s;

            case "5":
                s = "Solucionado";
                return s;

            case "6":
                s = "Cerrado";
                return s;

        }
        return s;
    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        if (respuesta.getInt("response") == 200) {
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("Etiquetas",content.toString());

        } else {

        }
    }
}