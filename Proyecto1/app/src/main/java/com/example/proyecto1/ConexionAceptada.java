package com.example.proyecto1;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.lista.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;




public class ConexionAceptada extends AppCompatActivity {

    ArrayList<String> countryList= new ArrayList<>();
    ArrayList<String> informacion= new ArrayList<>();
    RecyclerView lista;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public String contenido;
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            String contenido=informacion.get(position);
            accederTicket(view, contenido);

        }
    };

    JSONArray listado;

    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion_aceptada);
        try {
            conseguirTickets();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        lista= (RecyclerView)findViewById(R.id.simpleListView);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lista.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(countryList);
        lista.setAdapter(adapter);
        adapter.onItemClickListener(onItemClickListener);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }



    public void conseguirTickets() throws JSONException {

        mapa.put("peticion","listTicket");
        JSONObject tickets=pet.peticiones(mapa);
        listado=ClienteTFG.probando(tickets);
        if (listado == null){
            countryList.add("Sin tickets");
        } else {
            for (int i = 0; i < listado.length(); ++i) {
                JSONObject rec = listado.getJSONObject(i);
                countryList.add(rec.getString("title"));
                informacion.add(rec.getString("desc") + rec.getString("start_date")
                        +rec.getString("start_date") + rec.getString("ticket_status")
                        + rec.getString("ticket_owner"));

            }

        }

    }

    public void actualizar(MenuItem item){
        try {
            conseguirTickets();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lista= (RecyclerView)findViewById(R.id.simpleListView);
        lista.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lista.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(countryList);
        lista.setAdapter(adapter);
    }




    public void otraprueba(MenuItem item){

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent = new Intent (this,newticket.class);
        startActivity(intent);

    }




    public void desconectar(MenuItem item){

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mapa.put("peticion","exit");
        JSONObject peticiones=pet.peticiones(mapa);
        ClienteTFG.probando(peticiones);
        System.exit(0);
    }

    public void accederTicket(View v, String contenido){
        Intent intent = new Intent(this, ticket.class);
        intent.putExtra("texto", contenido);
        startActivity(intent);


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mapa.put("peticion","exit");
        JSONObject peticiones=pet.peticiones(mapa);
        ClienteTFG.probando(peticiones);
    }






}
