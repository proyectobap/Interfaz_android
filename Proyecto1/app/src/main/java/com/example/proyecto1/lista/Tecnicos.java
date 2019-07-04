package com.example.proyecto1.lista;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class Tecnicos extends AppCompatActivity implements RespuestaHilo {

    ArrayList<String> tecnicos;
    String tec;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    TextView text;
    SwipeRefreshLayout swipeRefreshLayout;
    Button asignar;
    Button desasignar;

    /*Esta clase nos proporcionará los técnicos para poder asignarlos a un ticket en cuestión.
    Tendremos el swipe para refrescar la información y el número del técnico asociado.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnicos);
        asignar= findViewById(R.id.add_tec);
        desasignar= findViewById(R.id.erase_tec);
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
        final EditText cajaTexto = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Añadir técnico")
                .setMessage("Escriba el número de técnico que quiere")
                .setView(cajaTexto)
                .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String tecnico= cajaTexto.getText().toString();
                        mapa.put("assigned_tech", tecnico);
                        JSONObject tecnicos = pet.peticiones(mapa);
                        realizar(tecnicos);

                    }

                })

                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }

/*Este método funciona muy parecido al anterior, solo que manda una petición para desasignar al técnico.
Al darle al botón, se mostrará una ventana que te pide el número de técnico que quieres desasignar.
 */
    public void desasignarTecnicos(View v){
        mapa.clear();
        mapa.put("peticion","DELETETECHASSIGNED");
        mapa.put("ticket_id", id_ticket);
        final EditText cajaTexto = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Desasignar técnico")
                .setMessage("Escriba el número de técnico que quiere desasignar")
                .setView(cajaTexto)
                .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String tecnico= cajaTexto.getText().toString();
                        mapa.put("assigned_tech", tecnico);
                        JSONObject tecnicos = pet.peticiones(mapa);
                        realizar(tecnicos);

                    }

                })

                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }

    public void realizar(JSONObject tecnicos){
        Informacion.getConexion().setInstruccion(tecnicos, this);
    }
//Con este método cambiamos la view para que se muestre el número del técnico asociado
    public void conseguirTecnicos(final JSONArray listado) throws Exception {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tec = "Número del técnico asociado:\n";
                try {
                    tec = tec + listado.getJSONObject(0).get("assigned_tech").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                text.setText(tec);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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
        conseguirTecnicos(content);
        }
    }

