package com.example.proyecto1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.lista.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Loading extends AppCompatActivity implements RespuestaHilo {

    public static ArrayList<String> users = new ArrayList<>();
    public static ArrayList<String> email = new ArrayList<>();
    public static ArrayList<String> user_type = new ArrayList<>();
    public static ArrayList<String> own_user = new ArrayList<>();
    public static ArrayList<String> own_email = new ArrayList<>();
    public static ArrayList<String> own_type = new ArrayList<>();

    public static ArrayList<String> titulos= new ArrayList<>();
    public static ArrayList<String> descripcion= new ArrayList<>();
    public static ArrayList<String> fecha_creacion= new ArrayList<>();
    public static ArrayList<String> fecha_modificacion= new ArrayList<>();
    public static ArrayList<String> estado_ticket= new ArrayList<>();
    public static ArrayList<String> ticket_id= new ArrayList<>();
    public static ArrayList<String> ticket_object= new ArrayList<>();
    public static ArrayList<String> ticket_owner= new ArrayList<>();
    Intent intent;

    Map<String, String> mapa = new LinkedHashMap<>();
    ProcesarPeticiones pet = new ProcesarPeticiones();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        try {
            peticionTickets();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        waiting();

    }

    public void waiting(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                peticionUsers();
            }
        }, 1500);
    }

    public void peticionUsers() {
        mapa.put("peticion", "listUsers");
        final JSONObject usuarios = pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(usuarios, this);
    }


    public void peticionTickets() throws JSONException {
        if (checkId()){
            mapa.put("peticion", "LISTTICKETFILTER");
            mapa.put("filter", "WHERE ticket_owner = " +
                    ClienteTFG.contenido.getJSONObject(0).getInt("user_id"));
            JSONObject tickets = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(tickets, this);
        } else {
            mapa.put("peticion", "listticket");
            JSONObject tickets = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(tickets, this);
        }

    }

    public void conseguirUsers(JSONArray listado) throws Exception {
        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            if (!checkId()) {
                if (rec.getInt("user_id") ==
                        ClienteTFG.contenido.getJSONObject(0).getInt("user_id")) {
                    own_user.add(rec.getString("name") + " " + rec.getString("last_name"));
                    own_email.add(rec.getString("email"));
                    own_type.add(rec.getString("user_type"));
                } else {
                    users.add(rec.getString("name") + " " + rec.getString("last_name"));
                    email.add(rec.getString("email"));
                    user_type.add(rec.getString("user_type"));
                }
            } else {
                if (rec.getInt("user_id") ==
                        ClienteTFG.contenido.getJSONObject(0).getInt("user_id")) {
                    own_user.add(rec.getString("name") + " " + rec.getString("last_name"));
                    own_email.add(rec.getString("email"));
                    own_type.add(rec.getString("user_type"));
                }
            }
            Intent intent = new Intent(this, Contenedor.class);
            startActivity(intent);
        }


    }

    public void conseguirTickets(JSONArray listado) throws Exception {
        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            titulos.add(rec.getString("title"));
            descripcion.add(rec.getString("desc"));
            fecha_creacion.add(rec.getString("create_time"));
            fecha_modificacion.add(rec.getString("mod_date"));
            estado_ticket.add(rec.getString("ticket_status_id"));
            ticket_id.add(rec.getString("ticket_id"));
            ticket_object.add(rec.getString("ticket_object"));
            ticket_owner.add(rec.getString("ticket_owner"));
        }


    }

    public boolean checkId() throws JSONException {
        return ClienteTFG.contenido.getJSONObject(0).getInt("user_type") <= 2;
    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        if (respuesta.getInt("response") == 200) {
            Log.e("ID", String.valueOf(ClienteTFG.contenido));
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("Etiquetas", content.toString());
            if (content.getJSONObject(0).has("name")){
                conseguirUsers(content);
            }
            else{
                conseguirTickets(content);
            }
        } else {
           Log.e("ERROR", "Algo ha fallado");
        }
    }
}
