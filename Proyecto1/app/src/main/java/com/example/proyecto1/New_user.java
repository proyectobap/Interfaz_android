package com.example.proyecto1;

import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class New_user extends AppCompatActivity implements RespuestaHilo, AdapterView.OnItemSelectedListener {

    TextInputEditText nombre;
    TextInputEditText apellido;
    TextInputEditText email;
    String user_id;
    String user_type;
    int numero_user_type;
    TextInputEditText login_name;
    TextInputEditText shdw_passwd;
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    Spinner spinner;
/*Esta será la clase para crear nuevos usuarios. Necesitaremos nombre de login, nombre, apellido,
email y contraseña. Si el usuario es administrador, también podremos crear un usuario de distintos
tipos con un spinner. Si no lo eres, solo se crearán clientes de forma predefinida
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        nombre = findViewById(R.id.nombre);
        apellido = findViewById(R.id.apellido);
        email = findViewById(R.id.email);
        login_name = findViewById(R.id.nombre_login);
        shdw_passwd = findViewById(R.id.contrasenia);
        if (Loading.own_type.equals("5")) {
            spinner = (Spinner) findViewById(R.id.user_type);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.user_types, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }
    }
/*Este método será la petición para crear el login del usuario ya creado. Pedirá el nombre del login,
la contraseña y el id. El id nos lo proporciona la respuesta del servidor al crear un usuario.
 */
    public void peticionLogin(){
        try {
            mapa.clear();
            mapa.put("peticion", "newlogin");
            mapa.put("login_name", login_name.getText().toString());
            mapa.put("shdw_passwd", shdw_passwd.getText().toString());
            mapa.put("user_id", user_id);
            JSONObject login_user = pet.peticiones(mapa);
            Informacion.getConexion().setInstruccion(login_user, this);
        } catch (NullPointerException e){
            Toast toast = Toast.makeText(this, "Rellene todos los campos, por favor",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }
//Este método crea el usuario con el nombre, apellido, email y tipo de usuario
    public void create_user(View v){
        mapa.clear();
        mapa.put("peticion", "newuser");
        mapa.put("email", email.getText().toString());
        mapa.put("name", nombre.getText().toString());
        mapa.put("lastname", apellido.getText().toString());
            if (Loading.own_type.equals("5")){
                mapa.put("user_type", String.valueOf(numero_user_type));
            }else{
                mapa.put("user_type", "2");
            }
            JSONObject user = pet.peticiones(mapa);
            Log.e("prueba", user.toString());
            Informacion.getConexion().setInstruccion(user, this);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    peticionLogin();
                }
            }, 1500);
            Toast toast = Toast.makeText(this, "User creado",
                    Toast.LENGTH_LONG);
            toast.show();

    }
    /*Este será nuestro listener para el spinner, que cogerá el valor del elemento elegido y lo asignará
    a una variable. El método estado creará un número para poder enviar de forma correcta la modificación.
    Este método usa un switch para elegir el número apropiado cada vez que haces uso del spinner.
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        user_type = String.valueOf(parent.getSelectedItem());
        estado(user_type);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    public int estado(String s){
        switch (s) {
            case "Cliente":
                numero_user_type = 2;
                return numero_user_type;
            case "Técnico":
                numero_user_type = 3;
                return numero_user_type;
            case "Supervisor":
                numero_user_type = 4;
                return numero_user_type;
            case "Administrador":
                numero_user_type = 5;
                return numero_user_type;

        }
        return numero_user_type;
    }


    @Override
    public void respuesta(JSONObject respuesta) throws JSONException {
        Log.e("ID", String.valueOf(ClienteTFG.contenido));
        JSONArray content = respuesta.getJSONArray("content");
        user_id = content.getJSONObject(0).getString("id");

    }
}
