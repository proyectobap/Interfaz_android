package com.example.proyecto1.lista;

import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Contenedor_tickets;
import com.example.proyecto1.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.Map;


public class Modificar_ticket extends AppCompatActivity implements RespuestaHilo, AdapterView.OnItemSelectedListener {

/*Esta clase servirá para modificar un ticket, por lo que necesitaremos todos los atributos del ticket
que hemos elegido para saber qué partes modificamos (y porque la petición necesita que se mande todos los
atributos). Mostraremos un spinner para seleccionar el estado, cogeremos la descripción, títutlo, dueño
y objeto.
 */
    TextInputEditText titulo;
    TextInputEditText des;
    Spinner spinner;
    String estado_ticket;
    int numero_estado_ticket;
    TextInputEditText ticket_owner;
    TextInputEditText ticket_object;
    Contenedor_tickets c;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    public static String id_ticket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_ticket);
        c= new Contenedor_tickets();
        spinner = (Spinner) findViewById(R.id.estado_ticket);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.estado_ticket, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        id_ticket = this.getIntent().getExtras().getString("id");
        Bundle s = this.getIntent().getExtras();
        initializeUI();

    }
/*Este método se usará para inicializar la interfaz. Cogerá la caja de título, descripción y dueño
del ticket y pondrá en ellas como texto el valor originario del ticket para poder modificarlo
 */
    private void initializeUI() {

        Bundle s = this.getIntent().getExtras();
        String title= s.getString("titulo");
        String descripcion= s.getString("descripcion");
        String owner = s.getString("ticket_owner");
        String object = s.getString("ticket_object");


        titulo = (TextInputEditText) findViewById(R.id.title);

        titulo.setText(title);

        des = (TextInputEditText) findViewById(R.id.desc);

        des.setText(descripcion);

        ticket_owner = (TextInputEditText) findViewById(R.id.owner);

        ticket_owner.setText(owner);

        ticket_object = (TextInputEditText) findViewById(R.id.object);

        ticket_object.setText(object);


    }

//Este método hará la petición con los contenidos de las cajas de texto y el valor del spinner
    public void cambiarTicket(View v){
        mapa.clear();
        mapa.put("peticion", "modifyticket");
        mapa.put("title", titulo.getText().toString());
        mapa.put("desc", des.getText().toString());
        mapa.put("ticket_status_id", String.valueOf(numero_estado_ticket));
        mapa.put("ticket_owner", ticket_owner.getText().toString());
        mapa.put("ticket_object", ticket_object.getText().toString());
        mapa.put("ticket_id", id_ticket);
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }

/*Este será el listener del spinner, que hará que, dependiendo del estado del ticket selecionado, tenga
un valor numérico u otro.
 */
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
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        if (respuesta.getInt("response") == 200) {
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("Etiquetas",content.toString());

        } else {

        }
    }
}