package com.example.proyecto1.adds;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
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
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Loading;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.proyecto1.lista.ticket.id_ticket;

public class NewEvent extends Fragment implements RespuestaHilo {

    /*Esta clase es para crear un nuevo evento. Crearemos una checkbox (para comprobar si es tarea),
    un textinput para la descripción y un botón para crear el evento
     */
    CheckBox check;
    TextInputEditText descripcion;
    Map<String, String> mapa = new LinkedHashMap<>();
    ProcesarPeticiones pet = new ProcesarPeticiones();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_new_event, container, false);
        descripcion = view.findViewById(R.id.descripcion);
        check = view.findViewById(R.id.checkbox);
        Button button = (Button) view.findViewById(R.id.botonevento);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_Ticket(view);
            }
        });
        return view;
    }

    //Este método crea un evento o una tarea, dependiendo de si la check box está marcada o no


    public void new_Ticket(View v) {
        try {
            mapa.clear();
            String desc = descripcion.getText().toString();
            if (check.isChecked()) {
                mapa.put("peticion", "newtask");
                if (desc.equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Rellene todos los campos",
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    mapa.put("event_desc", descripcion.getText().toString());
                    mapa.put("ticket_id", id_ticket);
                    mapa.put("time", String.valueOf(0));
                    mapa.put("is_done", String.valueOf(false));
                }
            } else {
                if (desc.equals("")) {
                    Toast toast = Toast.makeText(getContext(), "Rellene todos los campos",
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    mapa.put("peticion", "newevent");
                    mapa.put("event_desc", descripcion.getText().toString());
                    mapa.put("ticket_id", id_ticket);
                    mapa.put("event_type", String.valueOf(1));
                }
                JSONObject ticket = pet.peticiones(mapa);
                Informacion.getConexion().setInstruccion(ticket, this);
                Toast toast = Toast.makeText(getContext(), "Evento creado",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
        catch (NullPointerException e) {
            Toast toast = Toast.makeText(getContext(), "Rellene todos los campos, por favor",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }



    @Override
    public void respuesta(JSONObject respuesta) throws JSONException {
        if (respuesta.getInt("response") == 200) {
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("Etiquetas", content.toString());
        } else {
            if (respuesta.getInt("response") == 200) {
                JSONArray content = respuesta.getJSONArray("content");
                Log.e("Etiquetas", content.toString());
            }
        }
    }
}