package com.example.proyecto1.adds;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.R;
import com.example.proyecto1.lista.ticket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public class NewSoftware extends Fragment implements RespuestaHilo {

    TextInputEditText name;
    TextInputEditText developer;
    TextInputEditText version;

    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
//Esta petición servirá para crear un elemento software. Necesitaremos nombre, desarrollador y versión
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_new_software, container, false);
        name = view.findViewById(R.id.nombre_software);
        developer = view.findViewById(R.id.developer);
        version = view.findViewById(R.id.version);
        Button button = (Button) view.findViewById(R.id.botonsoftware);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new_Software();
            }
        });
        return view;
    }

    //Esta petición creará un nuevo elemento software, creando un json con la petición y lo necesario para mandarla
    public void new_Software(){
        try {
            mapa.clear();
            mapa.put("peticion", "newsoftware");
            mapa.put("internal_name", name.getText().toString());
            mapa.put("developer", developer.getText().toString());
            mapa.put("version", version.getText().toString());
            JSONObject software = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(software, this);
        } catch (NullPointerException e){
            Toast toast = Toast.makeText(getContext(), "Rellene todos los campos, por favor",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }
//Esta petición asignará dicho elemento creado al ticket que se tiene abierto en ese momento
    public void asignar_Software(String id){
        mapa.clear();
        mapa.put("peticion", "assignelement");
        mapa.put("ticket_id", ticket.id_ticket);
        mapa.put("element_id", id);
        JSONObject asignacion = pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(asignacion, this);
    }

    //El método asignar solamente se llamará cuando se llame al método de crear el elemento, ya que contiene un ID
    @Override
    public void respuesta(JSONObject respuesta) {
        // Recoger respuesta del servidor y procesarla
        try {
            if (respuesta.getInt("response") == 200) {
                JSONArray content = respuesta.getJSONArray("content");
                Log.e("Hola", content.getJSONObject(0).toString());
                if(content.getJSONObject(0).has("id")){
                    String id = content.getJSONObject(0).get("id").toString();
                    asignar_Software(id);
                }
            } if (respuesta.getInt("response") == 300) {
                Log.e("Probando", respuesta.getJSONArray("content").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}