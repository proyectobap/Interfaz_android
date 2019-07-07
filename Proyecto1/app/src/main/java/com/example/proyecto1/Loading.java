package com.example.proyecto1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Loading extends AppCompatActivity implements RespuestaHilo {

/*Esta clase servirá para crear la view de cargando. Hará las peticiones de la lista de los tickets
y la lista de usuarios. Crearemos un array list por cada atributo de los tickets y usuarios y las hará
estáticas por si necesitamos acceder a la información en cualquier momento.
 */
    public static ArrayList<String> users = new ArrayList<>();
    public static ArrayList<String> email = new ArrayList<>();
    public static ArrayList<String> user_type = new ArrayList<>();
    public static String own_user;
    public static String own_email;
    public static String own_type;
    public static String own_id;
    public static ArrayList<String> tickets_completos= new ArrayList<>();
    public static ArrayList<String> titulos= new ArrayList<>();
    public static ArrayList<String> descripcion= new ArrayList<>();
    public static ArrayList<String> fecha_creacion= new ArrayList<>();
    public static ArrayList<String> fecha_modificacion= new ArrayList<>();
    public static ArrayList<String> estado_ticket= new ArrayList<>();
    public static ArrayList<String> ticket_id= new ArrayList<>();
    public static ArrayList<String> ticket_object= new ArrayList<>();
    public static ArrayList<String> ticket_owner= new ArrayList<>();
    public static Map<String, String> tecnicos = new HashMap<>();
    public static Map<String, String> usuarios = new HashMap<>();
    Map<String, String> map_users = new LinkedHashMap<>();
    Intent intent;

    Map<String, String> mapa = new LinkedHashMap<>();
    ProcesarPeticiones pet = new ProcesarPeticiones();
/*Al crearse primero hacemos la petición de los clientes y después de los users. Las peticiones de los
users será segundo y medio después, para asegurarnos de que hemos conseguido todos los datos de forma
correcta.
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        try {
            peticionTickets();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        waiting();

    }

    public void waiting(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                peticionUsers();
            }
        }, 1500);
    }
//Este método pedirá la lista de usuarios
    public void peticionUsers() {
        mapa.put("peticion", "listUsers");
        final JSONObject usuarios = pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(usuarios, this);
    }

/*Este método obtendrá una lista de tickets en función de si eres cliente o no. Si eres cliente
solo te mostrará los tickets que contengan tu id de usuario. Si no lo eres, te mostrará todos los tickets
 */
    public void peticionTickets() throws JSONException {
        if (checkId()){
            String filter =  "WHERE ticket_owner = " +
                    ClienteTFG.contenido.getJSONObject(0).getInt("user_id");
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
/*Este método conseguirá los users. Si eres cliente, solo cogerá tu usuario. Si no lo eres, cogerá
Todos los datos de todos los usuarios.
 */
    public void conseguirUsers(JSONArray listado) throws Exception {
        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            usuarios.put(rec.getString("user_id"), rec.getString("name")+ " "+
                    rec.getString("last_name"));
            if(rec.getString("user_type").equals("3")){
                tecnicos.put(rec.getString("user_id"),
                        rec.getString("name")+ " "+ rec.getString("last_name"));
            }
            map_users.put(rec.getString("user_id"),
                    rec.getString("name")+rec.getString("last_name"));
            if (!checkId()) {
                if (rec.getInt("user_id") ==
                        ClienteTFG.contenido.getJSONObject(0).getInt("user_id")) {
                    own_user = rec.getString("name") + " " + rec.getString("last_name");
                    own_email = rec.getString("email");
                    own_type = rec.getString("user_type");
                } else {
                    users.add(rec.getString("name") + " " + rec.getString("last_name"));
                    email.add(rec.getString("email"));
                    user_type.add(rec.getString("user_type"));
                }
            } else {
                if (rec.getInt("user_id") ==
                        ClienteTFG.contenido.getJSONObject(0).getInt("user_id")) {
                    own_user = rec.getString("name") + " " + rec.getString("last_name");
                    own_email = rec.getString("email");
                    own_type = rec.getString("user_type");
                }
            }
            intent = new Intent(this, Contenedor.class);
            startActivity(intent);
        }


    }
//Este método cogerá todos los tickets que no estén cerrados (el estatus número 6)
    public void conseguirTickets(JSONArray listado) throws Exception {
        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            if(!rec.getString("ticket_id").equals("0")){
                if (!rec.getString("ticket_status_id").equals("6")) {
                    tickets_completos.add(rec.getString("title") + "\n" +
                            rec.getString("desc") + "\n" + rec.getString("mod_date"));
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


    }
//Comprobación de que el tipo de usuario es cliente (es 2 en la base de datos)
    public boolean checkId() throws JSONException {
        return ClienteTFG.contenido.getJSONObject(0).getInt("user_type") <= 2;
    }

//Al hacer una petición, dependiendo de si la respuesta contiene un atributo de ticket o de usuarios llamará a un método o a otro
    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        if (respuesta.getInt("response") == 200) {
            JSONArray content = respuesta.getJSONArray("content");
            own_id = ClienteTFG.contenido.getJSONObject(0).getString("user_id");
            Log.e("Etiquetas", content.toString());
            if (content.getJSONObject(0).has("name")){
                conseguirUsers(content);
            }
            else{
                conseguirTickets(content);
            }
        } else {
           Log.e("ERROR", "Algo ha fallado");
        }
    }
}
