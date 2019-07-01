package com.example.proyecto1;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class NewTicketAlone extends AppCompatActivity implements RespuestaHilo {

    TextInputEditText titulo;
    TextInputEditText descripcion;
    TextInputEditText estado;
    TextInputEditText object;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket_alone);
        titulo = findViewById(R.id.titulo);
        descripcion = findViewById(R.id.descripcion);
        estado = findViewById(R.id.status_id);
        object = findViewById(R.id.object);


    }

    public void newTicketAlone(View v){
        try {
            mapa.put("peticion", "newticket");
            mapa.put("title", titulo.getText().toString());
            mapa.put("desc", descripcion.getText().toString());
            mapa.put("ticket_status_id", estado.getText().toString());
            mapa.put("ticket_owner", String.valueOf(ClienteTFG.contenido.getJSONObject(0).getInt("user_id")));
            mapa.put("ticket_object", object.getText().toString());
            JSONObject ticket = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(ticket, this);
            Toast toast = Toast.makeText(this, "Ticket creado",
                    Toast.LENGTH_LONG);
            toast.show();
        } catch (NullPointerException e){
            Toast toast = Toast.makeText(this, "Rellene todos los campos, por favor",
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

