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
import android.widget.Button;
import android.widget.ImageButton;


import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Contenedor_tickets;
import com.example.proyecto1.Loading;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;




public class Lista_tickets extends Fragment implements RespuestaHilo {


    public Lista_tickets(){

    }

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
    ImageButton button;
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

    JSONArray listado;

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

    public void actualizar(View v){
        try {
            peticion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                peticion();
            }
        }, 500);
        lista= (RecyclerView)v.findViewById(R.id.simpleListView);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        MyAdapter adapter = new MyAdapter(titulos);
        lista.setAdapter(adapter);
        adapter.onItemClickListener(onItemClickListener);
        swipeRefreshLayout.setRefreshing(false);
    }

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


    public void peticion(){
        mapa.put("peticion","listTicket");
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }





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
