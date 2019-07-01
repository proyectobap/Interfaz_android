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

public class newticket extends Fragment implements RespuestaHilo {

    TextInputEditText titulo;
    TextInputEditText descripcion;
    TextInputEditText estado;
    TextInputEditText object;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_newticket, container, false);
        titulo = view.findViewById(R.id.titulo);
        descripcion = view.findViewById(R.id.descripcion);
        estado = view.findViewById(R.id.status_id);
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

    public void new_Ticket(View v){
        try {
            mapa.put("peticion", "newticket");
            mapa.put("title", titulo.getText().toString());
            mapa.put("desc", descripcion.getText().toString());
            mapa.put("ticket_status_id", estado.getText().toString());
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


    @Override
    public void respuesta(JSONObject respuesta) {

    }
}
