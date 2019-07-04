package com.example.proyecto1.lista;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class ticket extends Fragment implements RespuestaHilo {

/*Esta clase nos dará la información del ticket que elijamos en la lista de tickets. Tendremos la
descripción, título, id del ticket, fecha de creación, fecha de la última modificación, y estado
del ticket. También tendremos dos botones, uno para ir a la activity para modificar el ticket y otro
para poder saber el técnico asociado.
 */
    TextView mTextview;
    Intent intent;
    Contenedor_tickets c;
    ArrayList<String> tecnicos= new ArrayList<>();
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    public static String id_ticket;
    Button button;
    Button button2;
    SwipeRefreshLayout swipeRefreshLayout;
    public ticket(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_ticket, container, false);
        c= new Contenedor_tickets();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        id_ticket = getActivity().getIntent().getExtras().getString("id");
        final Bundle s = getActivity().getIntent().getExtras();

        initializeUI(view, s);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeUI(view, s);
                pedirTec();

            }
        });
        button = (Button) view.findViewById(R.id.modificar_ticket);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                modify(s.getString("titulo"), s.getString("descripcion"),
                        s.getString("ticket_owner"), s.getString("ticket_object"),
                        s.getString("estado"));
            }
        });

        button2 = (Button) view.findViewById(R.id.add_tec);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
              asignarTecnicos();
            }
        });

        intent= new Intent(getContext(), Tecnicos.class);
        String tec = "Número del técnico asociado:\n";
        intent.putExtra("tecnicos", tec);
        pedirTec();
        return view;

    }

//Con esta petición ya sabremos el técnico asociado del ticket, pero no lo mostrará a no ser que pasemos a la siguiente ventana
    public void pedirTec(){
        mapa.put("peticion","techRelation");
        mapa.put("ticket_id", id_ticket);
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }
//Este método inicializará la interfaz. Creará unos strings y los distintos campos que queremos mostrar.

    private void initializeUI(View v, Bundle s) {

        String id = "Id del ticket:\n" + id_ticket;
        String titulo = "\nTítulo:\n" + s.getString("titulo")+ "\n";
        String creacion= "\nFecha de creación:\n" + s.getString("fecha_creacion");
        String modificacion= "\nFecha de modificación:\n" + s.getString("fecha_creacion");
        String estado = "\nEstado:\n" + estado(s.getString("estado"));
        String stecnicos= "\nTécnicos asignados:\n" + tecnicos.toString();

        mTextview = (TextView)v.findViewById(R.id.ticket_id);

        mTextview.setText(id);

        mTextview = (TextView)v.findViewById(R.id.title);

        mTextview.setText(titulo);

        mTextview = (TextView)v.findViewById(R.id.desc);

        mTextview.setText(s.getString("descripcion"));

        mTextview = (TextView)v.findViewById(R.id.fecha_creacion);

        mTextview.setText(creacion);

        mTextview = (TextView)v.findViewById(R.id.fecha_modificacion);

        mTextview.setText(modificacion);

        mTextview = (TextView)v.findViewById(R.id.estado);

        mTextview.setText(estado);

        swipeRefreshLayout.setRefreshing(false);

    }
//Esto cambiará el número de estado por un string para que la información se entienda de forma correcta
    public String estado(String s){
        switch (s) {
            case "1":
                s = "Abierto";
                return s;

            case "2":
                s = "Asignado";
                return s;

            case "3":
                s = "En espera";
                return s;

            case "4":
                s = "";
                return s;

            case "5":
                s = "Solucionado";
                return s;

            case "6":
                s = "Cerrado";
                return s;

        }
        return s;
    }
//Este método cogerá los datos del ticket y los mandará a la clase de modificar ticket
    public void modify(String a, String e, String i, String o, String u){
        Intent intent = new Intent(getContext(), Modificar_ticket.class);
        intent.putExtra("id", id_ticket);
        intent.putExtra("titulo", a);
        intent.putExtra("descripcion", e);
        intent.putExtra("ticket_owner", i);
        intent.putExtra("ticket_object", o);
        intent.putExtra("estado", u);
        startActivity(intent);

    }


    public void asignarTecnicos(){
        startActivity(intent);
    }



    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        JSONArray content = respuesta.getJSONArray("content");
        if (respuesta.getInt("response") == 200) {
            Log.e("Etiquetas",content.toString());
            tecnicos.add(content.getJSONObject(0).getString("assigned_tech"));
            String tec = "Número del técnico asociado:\n";

            for (int x = 0; x < tecnicos.size(); x++) {
                tec = tec + tecnicos.get(x);
            }
            Log.e("tecnicos",tecnicos.toString());
            intent.putExtra("tecnicos", tec);
        } else {
        }
    }
}
