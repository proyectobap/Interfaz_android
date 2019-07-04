package com.example.proyecto1.adds;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Loading;
import com.example.proyecto1.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public class newticket extends Fragment implements RespuestaHilo, AdapterView.OnItemSelectedListener {

    TextInputEditText titulo;
    TextInputEditText descripcion;
    Spinner spinner;
    String estado_ticket;
    TextInputEditText object;
    int numero_estado_ticket;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();

    //Esta clase crea un ticket, que necesita un titulo, descripción, estado y objeto
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_newticket, container, false);
        titulo = view.findViewById(R.id.titulo);
        descripcion = view.findViewById(R.id.descripcion);
        spinner = (Spinner) view.findViewById(R.id.estado_ticket);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.estado_ticket, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        object = view.findViewById(R.id.object);
        Button button = (Button) view.findViewById(R.id.botonticket);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new_Ticket(view);
            }
        });
        return view;
    }

    //Con esta petición cogemos el texto de la activity y se lo añadimos a un json para hacer la petición pertinente
    public void new_Ticket(View v){
        try {
            mapa.put("peticion", "newticket");
            mapa.put("title", titulo.getText().toString());
            mapa.put("desc", descripcion.getText().toString());
            mapa.put("status_id", String.valueOf(numero_estado_ticket));
            mapa.put("ticket_owner", String.valueOf(ClienteTFG.contenido.getJSONObject(0).getInt("user_id")));
            mapa.put("ticket_object", object.getText().toString());
            JSONObject ticket = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(ticket, this);
            Toast toast = Toast.makeText(getContext(), "Ticket creado",
                    Toast.LENGTH_LONG);
            toast.show();
        } catch (NullPointerException e){
            Toast toast = Toast.makeText(getContext(), "Rellene todos los campos, por favor",
                    Toast.LENGTH_LONG);
            toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        estado_ticket = String.valueOf(parent.getSelectedItem());
        estado(estado_ticket);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    public int estado(String s){
        switch (s) {
            case "Abierto":
                numero_estado_ticket = 1;
                return numero_estado_ticket;
            case "Asignado":
                numero_estado_ticket = 2;
                return numero_estado_ticket;
            case "En espera":
                numero_estado_ticket = 3;
                return numero_estado_ticket;
            case "Solucionado":
                numero_estado_ticket = 5;
                return numero_estado_ticket;
            case "Cerrado":
                numero_estado_ticket = 6;
                return numero_estado_ticket;

        }
        return numero_estado_ticket;
    }


    @Override
    public void respuesta(JSONObject respuesta) {

    }
}
