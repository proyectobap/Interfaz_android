package com.example.proyecto1.lista;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.proyecto1.lista.ticket.id_ticket;


public class Eventos extends Fragment implements RespuestaHilo {


    public Eventos(){

    }
/*Este fragment nos enseñará una recycler view con la lista de los eventos del ticket que abramos.
Cogeremos todos los atributos de los eventos en distintos arrays para poder saber los datos de cada
uno. Además, creamos un listener para cuando pulsemos en algún elemento de la lista; esto nos llevará
a los detalles del evento en cuestión.
 */
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> descripcion= new ArrayList<>();
    ArrayList<String> fecha_creacion= new ArrayList<>();
    ArrayList<String> fecha_modificacion= new ArrayList<>();
    ArrayList<String> event_type= new ArrayList<>();
    ArrayList<String> event_id= new ArrayList<>();
    Map<String, String> time= new LinkedHashMap<>();
    Map<String, Boolean> is_done= new LinkedHashMap<>();
    RecyclerView lista;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public String contenido;
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            String sdescripcion = descripcion.get(position);
            String sevent_type = event_type.get(position);
            String sevent_id = event_id.get(position);
            intent.putExtra("descripcion", sdescripcion);
            intent.putExtra("estado", sevent_type);
            intent.putExtra("event_id", sevent_id);
            if (sevent_type.equals("2")){
                intent.putExtra("time",time.get(sevent_id));
                intent.putExtra("is_done", is_done.get(sevent_id));
                Log.e("hola", String.valueOf(is_done.get(sevent_id)));
            }

            accederEvento(view);

        }
    };

    Intent intent;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    /*Al crearse la view, se esperará un segundo para que la petición no se mezcle con el resto,
     ya que es un fragment que tendrá otros dos al lado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_eventos, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        intent = new Intent(getContext(), Evento.class);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                peticion();
            }
        }, 1000);

        inicialite(view);



        return view;


    }

//Este método es para inicializar la lista de la recycler view y adaptarla a las descripciones de los eventos

    public void inicialite(View v){
        lista= (RecyclerView)v.findViewById(R.id.simpleListView);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyAdapter(descripcion);
        lista.setAdapter(adapter);
        adapter.onItemClickListener(onItemClickListener);

    }

/*Con este método conseguimos los eventos una vez realizada la petición (ya que la respuesta de la
petición llamará a este método). Miraremos el tipo de evento, y si no es 4 seguirá rellenando las listas,
ya que el 4 es el estado de cerrado. Si es 2 es una tarea, y como tiene distintos atributos que un evento
normal, relacionaremos el id de ese evento para recoger sus atributos correctamente y poder acceder
a ellos de forma correcta
 */
    public void conseguirEventos(JSONArray listado) throws Exception {
        descripcion.clear();
        fecha_creacion.clear();
        fecha_modificacion.clear();
        event_id.clear();
        event_type.clear();
        time.clear();
        is_done.clear();
                for (int i = 0; i < listado.length(); ++i) {
                    JSONObject rec = listado.getJSONObject(i);
                    if (!rec.getString("event_type").equals("4")) {
                        descripcion.add(rec.getString("event_desc"));
                        event_id.add(rec.getString("event_id"));
                        event_type.add(rec.getString("event_type"));
                        if (rec.getString("event_type").equals("2")) {
                            Log.e("Evento", rec.toString());
                            time.put(rec.getString("event_id"), rec.getString("time"));
                            is_done.put((rec.getString("event_id")), rec.getBoolean("is_done"));
                            Log.e("map", String.valueOf(is_done.get(rec.getString("event_id"))));
                        }
                    }
                }

                lista.setAdapter(adapter);


    }


    public void peticion(){
        mapa.put("peticion","listevents");
        mapa.put("ticket_id", id_ticket);
        JSONObject eventos=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(eventos, this);
    }





    public void accederEvento(View v){

        startActivity(intent);


    }



    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        JSONArray content = respuesta.getJSONArray("content");
        if (respuesta.getInt("response") == 200) {
            Log.e("Etiquetas",content.toString());
            conseguirEventos(content);
        } else {
            Log.e("Etiquetas",content.toString());
        }
    }





}
