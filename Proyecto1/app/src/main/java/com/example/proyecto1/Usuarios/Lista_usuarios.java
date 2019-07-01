package com.example.proyecto1.Usuarios;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
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
import com.example.proyecto1.Loading;
import com.example.proyecto1.R;
import com.example.proyecto1.User;
import com.example.proyecto1.lista.MyAdapter;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Lista_usuarios extends Fragment implements RespuestaHilo {

    public Lista_usuarios(){

    }

    private SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<String> users= new ArrayList<>();

    ArrayList<String> email= new ArrayList<>();

    ArrayList<String> user_type= new ArrayList<>();

    RecyclerView lista;
    View v;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public String contenido;
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            String user = users.get(position);
            String semail = email.get(position);
            String type = user_type.get(position);

            accederUsuario(view, user, semail, type);

        }
    };



    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_conexion_aceptada, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        super.onCreate(savedInstanceState);
        getExtras(view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actualizar(view);
            }
        });

        return view;


    }





    public void conseguirUsers(JSONArray listado) throws Exception {
            users.clear();
            email.clear();
            user_type.clear();
            for (int i = 0; i < listado.length(); ++i) {
                JSONObject rec = listado.getJSONObject(i);
                users.add(rec.getString("name") + " " + rec.getString("last_name"));
                email.add(rec.getString("email"));
                user_type.add(rec.getString("user_type"));

        }

    }

    public void getExtras(View v){
        users = Loading.users;
        email = Loading.email;
        user_type = Loading.user_type;
        lista= (RecyclerView)v.findViewById(R.id.simpleListView);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        MyAdapter adapter = new MyAdapter(users);
        lista.setAdapter(adapter);
        adapter.onItemClickListener(onItemClickListener);
        swipeRefreshLayout.setRefreshing(false);

    }

    public void peticion(){
        mapa.put("peticion","listUsers");
        JSONObject usuarios=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(usuarios, this);
    }

    public void actualizar(View v){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                peticion();
            }
        }, 1500);
        lista= (RecyclerView)v.findViewById(R.id.simpleListView);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        lista.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyAdapter adapter = new MyAdapter(users);
        lista.setAdapter(adapter);
        adapter.onItemClickListener(onItemClickListener);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void accederUsuario(View v, String a, String e, String i){
        Intent intent = new Intent(getActivity(), User.class);
        intent.putExtra("user", a);
        intent.putExtra("email", e);
        intent.putExtra("user_type", i);
        startActivity(intent);


    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        Log.d("PRUEBA", "Estos son los usuarios");
        if (respuesta.getInt("response") == 200) {
            Log.e("ID", String.valueOf(ClienteTFG.contenido));
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("Etiquetas",content.toString());
            conseguirUsers(content);
        } else {

        }
    }
}
