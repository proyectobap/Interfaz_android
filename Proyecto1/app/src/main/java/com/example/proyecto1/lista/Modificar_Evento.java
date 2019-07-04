package com.example.proyecto1.lista;

import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.proyecto1.lista.Evento.event_type;
import static com.example.proyecto1.lista.Evento.id_event;
import static com.example.proyecto1.lista.ticket.id_ticket;

public class Modificar_Evento extends AppCompatActivity implements RespuestaHilo, AdapterView.OnItemSelectedListener {

/*Esta clase nos permite modificar un evento que hayamos seleccionado. El spinner será para selaccionar
unos estados en cuestión.
 */
    Spinner spinner;
    String sdescripcion;
    TextInputEditText descripcion;
    CheckBox done;
    Bundle s;
    Boolean is_done;
    String estado_evento;
    int numero_estado_evento;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar__evento);
        is_done=false;
        s = this.getIntent().getExtras();
        initializeUI(s);

    }
/*Este método inicializará la interfaz de forma distinta si el evento es tarea o no. Si es tarea, el spinner
no se verá y habrá una checkbox para marcar la tarea como resuelta o no.
 */
    private void initializeUI(Bundle s) {
        done = findViewById(R.id.check_task);
        if (event_type.equals("2")){
            done.setVisibility(View.VISIBLE);
        }else{
            spinner = (Spinner) findViewById(R.id.tipo_evento);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.types_events, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            done.setVisibility(View.INVISIBLE);
        }

        sdescripcion = s.getString("descripcion");

        descripcion = (TextInputEditText) findViewById(R.id.desc);

        descripcion.setText(sdescripcion);



    }

/*Esta petición modifica de forma distinta la tarea y el evento (ya que no son las mismas peticiones).
Si es tipo de evento es 2, mandará una petición para modificar una tarea y comprobará el check de la caja.
Si no es de tipo 2, comprobará el valor del spinner.
 */
    public void peticion(View v){
        if (event_type.equals("2")){
            mapa.clear();
            mapa.put("peticion", "modifytask");
            mapa.put("time", "0");
            if (done.isChecked()){
                is_done=true;
            }else{
            }
            mapa.put("is_done", String.valueOf(is_done));
            Log.e("done", String.valueOf(is_done));
            mapa.put("event_id", id_event);
            peticionTareas();

        }else{
            mapa.put("peticion", "modifyevent");
            mapa.put("event_desc", descripcion.getText().toString());
            mapa.put("ticket_id", id_ticket);
            mapa.put("event_type", String.valueOf(numero_estado_evento));
            mapa.put("event_id", id_event);
            JSONObject evento = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(evento, this);
        }

    }

    public void peticionTareas(){
        JSONObject tarea = pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tarea, this);
    }

/*Este será nuestro listener para el spinner, que cogerá el valor del elemento elegido y lo asignará
a una variable. El método estado creará un número para poder enviar de forma correcta la modificación.
Este método usa un switch para elegir el número apropiado cada vez que haces uso del spinner.
 */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
       estado_evento = String.valueOf(parent.getSelectedItem());
       estado(estado_evento);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    public int estado(String s){
        switch (s) {
            case "Abierto":
                numero_estado_evento = 1;
                return numero_estado_evento;
            case "Seguimiento":
                numero_estado_evento = 3;
                return numero_estado_evento;
            case "Cerrado":
                numero_estado_evento = 4;
                return numero_estado_evento;

        }
        return numero_estado_evento;
    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla


    }
}
