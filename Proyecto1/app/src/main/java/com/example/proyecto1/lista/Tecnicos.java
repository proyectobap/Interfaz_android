package com.example.proyecto1.lista;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Loading;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.proyecto1.lista.ticket.id_ticket;

public class Tecnicos extends AppCompatActivity implements RespuestaHilo, AdapterView.OnItemSelectedListener {

    ArrayList<String> tecnicos_array = new ArrayList<>();
    ArrayList<String> ids_tecnicos_array = new ArrayList<>();
    ArrayList<String> tecnicos_asociados = new ArrayList<>();
    ArrayList<String> desasignar_tecnicos_array = new ArrayList<>();
    ArrayList<String> desasignar_ids_tecnicos_array = new ArrayList<>();
    String tec;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    TextView text;
    SwipeRefreshLayout swipeRefreshLayout;
    Button asignar;
    Button desasignar;
    Spinner tecnicos;
    Spinner tecnicos_asignados;
    ArrayAdapter<String> comboAdapter;
    String tecnico_elegido;
    Object object;
    String tecnico_elegido_desasignar;


    /*Esta clase nos proporcionará los técnicos para poder asignarlos a un ticket en cuestión.
    Tendremos el swipe para refrescar la información y el número del técnico asociado.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnicos);
        asignar= findViewById(R.id.add_tec);
        desasignar= findViewById(R.id.erase_tec);
        tecnicos = findViewById(R.id.spinner_tecnicos);
        tecnicos.setOnItemSelectedListener(this);
        tecnicos_asignados = findViewById(R.id.spinner_tecnicos_desasignar);
        tecnicos_asignados.setOnItemSelectedListener(this);
        crear_spinner();
        crear_spinner_asociados();
        if (Loading.own_type.equals("2")){
            asignar.setVisibility(View.INVISIBLE);
            desasignar.setVisibility(View.INVISIBLE);
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pedirTec();
            }
        });
        Bundle s = this.getIntent().getExtras();
        tec = s.getString("tecnicos");
        text = findViewById(R.id.tecnicos);
        text.setText(tec);

    }

/*Este método servirá para asignar un técnico al ticket. Se mostrará una caja de alerta al hacer click
en el botón de asignar técnico que te pedirá el número de técnico para asignar.
 */
    public void asignarTecnicos(View v){
        mapa.clear();
        mapa.put("peticion","assigntech");
        mapa.put("ticket_id", id_ticket);
        mapa.put("assigned_tech", tecnico_elegido);
        JSONObject tecnicos = pet.peticiones(mapa);
        realizar(tecnicos);

    }

    public void crear_spinner(){
String key;
String value;
        for (Map.Entry<String, String> entry  : Loading.tecnicos.entrySet()){
            key = entry.getKey();
            value = entry.getValue();
            ids_tecnicos_array.add(key);
            tecnicos_array.add(value);

        }
        comboAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, tecnicos_array);
        tecnicos.setAdapter(comboAdapter);

    }
    public void crear_spinner_asociados(){
        String key;
        String value;
        for (Map.Entry<String, String> entry  : Loading.tecnicos.entrySet()){
            key = entry.getKey();
            value = entry.getValue();
            if (ticket.tecnicos.contains(value)){
                desasignar_ids_tecnicos_array.add(key);
                desasignar_tecnicos_array.add(value);
            }

        }
        comboAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, desasignar_tecnicos_array);
        tecnicos_asignados.setAdapter(comboAdapter);
    }

/*Este método funciona muy parecido al anterior, solo que manda una petición para desasignar al técnico.
Al darle al botón, se mostrará una ventana que te pide el número de técnico que quieres desasignar.
 */
    public void desasignarTecnicos(View v){
        mapa.clear();
        mapa.put("peticion","DELETETECHASSIGNED");
        mapa.put("ticket_id", id_ticket);
        mapa.put("assigned_tech", tecnico_elegido_desasignar);
        JSONObject tecnico_fuera = pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tecnico_fuera, this);
    }

    public void crearToast(){
        Toast toast = (Toast) Toast.makeText(this, "Este técnico no está asociado",
                Toast.LENGTH_LONG);
        toast.show();
    }
    public void realizar(JSONObject tecnicos){
        Informacion.getConexion().setInstruccion(tecnicos, this);
    }



//Con este método sabemos el id del técnico que tenga asociado el ticket
    public void pedirTec(){
        mapa.clear();
        mapa.put("peticion","techRelation");
        mapa.put("ticket_id", id_ticket);
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        JSONArray content = respuesta.getJSONArray("content");
        Log.e("respuesta", respuesta.toString());
        if (respuesta.getInt("response") == 200){
            finish();
        } else {
            Log.e("estoy pasando por aquí", "hello");
            Snackbar.make(asignar,"Técnico ya asignado", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_tecnicos:
                tecnico_elegido = ids_tecnicos_array.get(position);
                Log.e("hola", tecnico_elegido);
                break;
            case R.id.spinner_tecnicos_desasignar:
                tecnico_elegido_desasignar = desasignar_ids_tecnicos_array.get(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

