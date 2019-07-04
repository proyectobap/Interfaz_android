package com.example.proyecto1.lista;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import static com.example.proyecto1.lista.ticket.id_ticket;

public class Evento extends AppCompatActivity {
    TextView mTextview;
    Intent intent;
    String id;
    public static String id_event;
    Button button;
    String descripcion;
    public static String event_type;
    public static Boolean is_done;
    String done;
/*Esta será la ventana en la que se mostrará lo que contenga el evento. Inicializaremos la
interfaz y crearemos un botón con un listener por si queremos modificar dicho ticket (pasando a otra ventana)
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        id = id_ticket;
        final Bundle s = this.getIntent().getExtras();
        id_event = s.getString("event_id");
        initializeUI(s);
        button = (Button) findViewById(R.id.botonevento);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                modify(s.getString("id"), s.getString("descripcion"));
            }
        });

    }

    private void initializeUI(Bundle s) {

        descripcion = s.getString("descripcion");
        Log.e("probando", descripcion);
        event_type = s.getString("estado");

        mTextview = (TextView) findViewById(R.id.desc);

        mTextview.setText(descripcion);

        mTextview = (TextView) findViewById(R.id.event_type);

        mTextview.setText(estado(event_type));

        if (event_type.equals("2")){
            is_done = s.getBoolean("is_done");
            Log.e("probando", is_done.toString());
            mTextview = (TextView) findViewById(R.id.is_done);
            mTextview.setText(done(is_done));
        }

    }

//Debido a que done es un boolean, este método transforma el true en Tarea resuelta y el false en Tarea no resuelta
    public String done(Boolean b){
        if (b){
            done = "Tarea resuelta";
            return done;
        }else{
            done = "Tarea no resuelta";
            return done;
        }
    }

    /*Esto transformará el estado del evento dependiendo de su número. El 2 no se muestra porque
    ese tipo convierte al evento en una tarea, que tiene otros atributos y tabla en la base
     */
    public String estado(String s){
        switch (s) {
            case "1":
                s = "Abierto";
                return s;

            case "2":
                s = "\n";
                return s;


            case "3":
                s = "Seguimiento";
                return s;

        }
        return s;
    }
/*Este método hace que pasemos a la ventana de modificar datos. Nos llevamos los datos
 necesarios para saber cómo estaban antes de cambiarlos
 */
    public void modify(String a, String e){
        intent = new Intent(this, Modificar_Evento.class);
        intent.putExtra("id", id_ticket);
        intent.putExtra("id_evento",a);
        intent.putExtra("descripcion", e);
        intent.putExtra("event_type", event_type);
        if (event_type.equals("2")){
            intent.putExtra("is_done", is_done);
        }
        startActivity(intent);

    }

}
