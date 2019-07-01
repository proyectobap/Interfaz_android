package com.example.proyecto1.lista;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class Elements extends Fragment implements RespuestaHilo {
    TextView mTextview;
    Contenedor_tickets c;
    ArrayList<String> elementos= new ArrayList<>();
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> ids= new ArrayList<>();
    View view;

    public Elements(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_elements, container, false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                peticionElementos();
            }
        }, 1000);


        mTextview = (TextView)view.findViewById(R.id.elementos);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        initializeUI();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                peticionElementos();
            }
        });

        return view;

    }

    public void initializeUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (elementos.size() == 0){
                    mTextview.setText("Sin elementos");
                }else{
                    mTextview.setText(elementos.get(0));
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });




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

    public void peticionElementos(){
        mapa.clear();
        mapa.put("peticion","ELEMENTRELATION");
        mapa.put("ticket_id", getActivity().getIntent().getExtras().getString("id"));
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }

    public void conseguirElementoHardware(JSONArray listado) throws Exception {
        elementos.clear();

        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            elementos.add("Nombre:\n" + rec.getString("internal_name") + "\nS/N:\n" +
                    rec.getString("S/N") + "\nBrand:\n" + rec.getString("brand") +
                    "\nModel:\n" + rec.getString("model"));

        }

        initializeUI();

    }

    public void conseguirElementoSoftware(JSONArray listado) throws Exception {
        elementos.clear();

        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            elementos.add("Nombre:\n" + rec.getString("internal_name") + "\nDeveloper:\n" +
                    rec.getString("developer") + "\nVersion:\n" + rec.getString("version"));
        }

        initializeUI();

    }

    public void peticionElemento(String ids){
        mapa.clear();
        mapa.put("peticion","ELEMENTDETAILS");
        mapa.put("element_id", ids);
        JSONObject elemento=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(elemento, this);
    }

    public void waiting(final String ids){
        peticionElemento(ids);
    }
    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        if (respuesta.getInt("response") == 200) {
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("peticion", content.toString());
            if (content.getJSONObject(0).has("brand")){
                conseguirElementoHardware(content);
                Log.e("hola","paso por aqui");
            }
            if (content.getJSONObject(0).has("developer")){
                conseguirElementoSoftware(content);
            } else {
                ids.clear();
                ids.add(content.getJSONObject(0).getString("element_id"));
                ids.add(content.getJSONObject(1).getString("element_id"));
                Log.e("prueba",ids.toString());
                Log.e("otraprueba", String.valueOf(ids.size()));
                for (int i = 0; i < 2; ++i) {
                    waiting(ids.get(i));
                    Log.e("prueba",ids.toString());
                }
            }

        } else {
            Log.e("fallo",respuesta.toString());
        }
    }
}
