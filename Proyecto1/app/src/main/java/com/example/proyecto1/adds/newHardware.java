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

public class newHardware extends Fragment implements RespuestaHilo {
//Con esta clase se crea un nuevo elemento hardware.
    TextInputEditText name;
    TextInputEditText s;
    TextInputEditText brand;
    TextInputEditText model;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_new_hardware, container, false);
        name = v.findViewById(R.id.nombre_hardware);
        s = v.findViewById(R.id.sn);
        brand = v.findViewById(R.id.brand);
        model = v.findViewById(R.id.model);
        Button button = (Button) v.findViewById(R.id.botonhardware);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new_Hardware();
            }
        });
        return v;
    }
//Esta será la petición para hacer un elemento, meteremos en la petición el nombre, el serial, bran y modelo
    public void new_Hardware(){
        try {
            mapa.put("peticion", "newhardware");
            mapa.put("internal_name", name.getText().toString());
            mapa.put("S/N", s.getText().toString());
            mapa.put("brand", brand.getText().toString());
            mapa.put("model", model.getText().toString());
            JSONObject hardware = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(hardware, this);
        } catch (NullPointerException e){
            Toast toast = Toast.makeText(getContext(), "Rellene todos los campos, por favor",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

//Esta petición asignará un hardware al ticket abierto en el momento de hacer el elemento
    public void asignar_Hardware(String id){
        mapa.clear();
        mapa.put("peticion", "assignelement");
        mapa.put("ticket_id", ticket.id_ticket);
        mapa.put("element_id", id);
        JSONObject asignacion = pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(asignacion, this);
    }

//La petición asignar_Hardware solo se activará cuando se cree un nuevo elemento, ya que se pedirá el id que devuelve esa petición
    @Override
    public void respuesta(JSONObject respuesta) {
        // Recoger respuesta del servidor y procesarla
        try {
            if (respuesta.getInt("response") == 200) {
                JSONArray content = respuesta.getJSONArray("content");
                if (content.getJSONObject(0).has("id")){
                    String id = content.getJSONObject(0).getString("id");
                    asignar_Hardware(id);
                }

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
