package com.example.proyecto1.lista;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Contenedor_tickets;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class Eventos extends Fragment implements RespuestaHilo {
    ArrayList<String> elements;
    TextView mTextview;
    Contenedor_tickets c;
    ArrayList<String> tecnicos= new ArrayList<>();
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();

    public Eventos(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_ticket, container, false);

        return view;

    }

    private void initializeUI(View v) {

        Bundle s = getActivity().getIntent().getExtras();
        String creacion= "\nFecha de creacion:\n" + s.getString("fecha_creacion");
        String modificacion= "\nFecha de modificacion:\n" + s.getString("fecha_creacion");
        String estado = "\nEstado:\n" + estado(s.getString("estado"));

        mTextview = (TextView)v.findViewById(R.id.desc);

        mTextview.setText(s.getString("descripcion"));

        mTextview = (TextView)v.findViewById(R.id.fecha_creacion);

        mTextview.setText(creacion);

        mTextview = (TextView)v.findViewById(R.id.fecha_modificacion);

        mTextview.setText(modificacion);

        mTextview = (TextView)v.findViewById(R.id.estado);

        mTextview.setText(estado);

        mTextview = (TextView)v.findViewById(R.id.tecnicos);

        mTextview.setText(mostrarTecnicos(tecnicos));

        peticionElementos();



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

    public void peticionTecnicos(){
        mapa.put("peticion","techRelation");
        mapa.put("ticket_id", getActivity().getIntent().getExtras().getString("id"));
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }

    public void peticionElementos(){
        mapa.put("peticion", "LISTELEMENTTYPE");
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }

    public void conseguirTecnicos(JSONArray listado) throws Exception {
        tecnicos.clear();

        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            tecnicos.add(rec.getString("assigned_tech"));

        }

    }

    public void conseguirElementos(JSONArray listado) throws JSONException {
        elements.clear();

        for (int i = 0; i < listado.length(); ++i){
            JSONObject rec = listado.getJSONObject(i);
            elements.add(rec.getString("element_id") + " " + rec.getString("internal_name") );
        }
    }

    public String mostrarTecnicos(ArrayList<String> t){
        String tec = "";
        for(int x=0;x<t.size();x++) {
            tec = tec + "\n" + t.get(x);
        }

        return tec;
    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        if (respuesta.getInt("response") == 200) {
            Log.e("ID", String.valueOf(ClienteTFG.contenido));
            JSONArray content = respuesta.getJSONArray("content");
            if (content.getJSONObject(0).has("element_id")){
                conseguirElementos(content);
            }
            else{
                Log.e("Etiquetas",content.toString());
                conseguirTecnicos(content);
            }

        }
    }
}

