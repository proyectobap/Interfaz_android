package com.example.proyecto1.lista;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Contenedor_tickets;
import com.example.proyecto1.Loading;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;




public class Lista_tickets extends Fragment implements RespuestaHilo {


    public Lista_tickets(){

    }
/*Con este fragment expondremos nuestros tickets en una recycler view de forma correcta. Crearemos
distintos arraylist para recoger todos los atributos de los estados, un SwipeRefresLayout para poder
actualiza la lista, un adapter para el recycler view y un listener para la view para poder elegir
un ticket y pasar a la vista del ticket con toda su información
 */
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> titulos= new ArrayList<>();
    ArrayList<String> descripcion= new ArrayList<>();
    ArrayList<String> fecha_creacion= new ArrayList<>();
    ArrayList<String> fecha_modificacion= new ArrayList<>();
    ArrayList<String> estado_ticket= new ArrayList<>();
    ArrayList<String> ticket_id= new ArrayList<>();
    ArrayList<String> ticket_object= new ArrayList<>();
    ArrayList<String> ticket_owner= new ArrayList<>();
    RecyclerView lista;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public String contenido;
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            String titulo = titulos.get(position);
            String sdescripcion = descripcion.get(position);
            String sfecha_creacion = fecha_creacion.get(position);
            String sfecha_modificacion = fecha_modificacion.get(position);
            String sestado_ticket = estado_ticket.get(position);
            String sticket_id = ticket_id.get(position);
            String sticket_owner= ticket_owner.get(position);
            String sticket_object = ticket_object.get(position);
            accederTicket(view, sdescripcion, sfecha_creacion, sfecha_modificacion, sestado_ticket,
                    sticket_id, titulo, sticket_owner, sticket_object);

        }
    };



    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_conexion_aceptada, container, false);
        final View v = inflater.inflate(R.layout.item, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        super.onCreateView(inflater, container, savedInstanceState);
        getExtras(view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actualizar(view);
            }
        });
        return view;


    }

/*Con este método actualizaremos la lista creando una nueva petición y después haciendo que la lista
se actualice con esta nueva información
 */
    public void actualizar(View v){
        try {
            peticion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lista= (RecyclerView)v.findViewById(R.id.simpleListView);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        MyAdapter adapter = new MyAdapter(titulos);
        lista.setAdapter(adapter);
        adapter.onItemClickListener(onItemClickListener);
        swipeRefreshLayout.setRefreshing(false);
    }

/*Este método nos sirve para coger toda la información que ha dejado la ventana de loading. Cogeremos
Cada atributo con variables static creadas en la anterior vista.
 */
    public void getExtras(View v){
        titulos = Loading.titulos;
        descripcion = Loading.descripcion;
        fecha_creacion = Loading.fecha_creacion;
        fecha_modificacion = Loading.fecha_modificacion;
        estado_ticket = Loading.estado_ticket;
        ticket_id = Loading.ticket_id;
        ticket_object = Loading.ticket_object;
        ticket_owner = Loading.ticket_owner;
        lista= (RecyclerView)v.findViewById(R.id.simpleListView);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        MyAdapter adapter = new MyAdapter(titulos);
        lista.setAdapter(adapter);
        adapter.onItemClickListener(onItemClickListener);
        swipeRefreshLayout.setRefreshing(false);

    }
/*Este método nos dará los tickets una vez realizada la petición (que se realiza al actualizar la página,
no al crearse). No cogerá ningún ticket que tenga de status id 6, porque esos son tickets cerrados.
 */
    public void conseguirTickets(JSONArray listado) throws Exception {
        titulos.clear();
        descripcion.clear();
        fecha_creacion.clear();
        fecha_modificacion.clear();
        estado_ticket.clear();
        ticket_id.clear();
        ticket_object.clear();
        ticket_owner.clear();

        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            if (!rec.getString("ticket_status_id").equals("6")) {
                titulos.add(rec.getString("title"));
                descripcion.add(rec.getString("desc"));
                fecha_creacion.add(rec.getString("create_time"));
                fecha_modificacion.add(rec.getString("mod_date"));
                estado_ticket.add(rec.getString("ticket_status_id"));
                ticket_id.add(rec.getString("ticket_id"));
                ticket_object.add(rec.getString("ticket_object"));
                ticket_owner.add(rec.getString("ticket_owner"));
            }

            }

    }

/*Este método obtendrá una lista de tickets en función de si eres cliente o no. Si eres cliente
    solo te mostrará los tickets que contengan tu id de usuario. Si no lo eres, te mostrará todos los tickets
 */
    public void peticion() {
        if (checkId()){
            String filter =  "WHERE ticket_owner = " +
                    Loading.own_id;
            Log.e("filter",filter);
            mapa.put("peticion", "LISTTICKETFILTER");
            mapa.put("filter", filter);
            JSONObject tickets = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(tickets, this);
        } else {
            mapa.put("peticion", "listticket");
            JSONObject tickets = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(tickets, this);
        }
    }

    //Comprobación de que el tipo de usuario es cliente (es 2 en la base de datos)
    public boolean checkId() {
        return Loading.own_type.equals("2");
    }




/*Este método creará el intent de un ticket en particular y cogerá la información que le pasemos al
hacer click en un ticket en particular.
 */
    public void accederTicket(View v, String a, String e, String i, String o, String u, String t,
                              String ow, String ob){
        Intent intent = new Intent(getContext(), Contenedor_tickets.class);
        intent.putExtra("titulo", t);
        intent.putExtra("descripcion", a);
        intent.putExtra("fecha_creacion", e);
        intent.putExtra("fecha_modificacion", i);
        intent.putExtra("estado", o);
        intent.putExtra("id", u);
        intent.putExtra("ticket_owner", ow);
        intent.putExtra("ticket_object", ob);
        startActivity(intent);


    }



    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        if (respuesta.getInt("response") == 200) {
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("Etiquetas",content.toString());
            conseguirTickets(content);
        } else {

        }
    }





}
