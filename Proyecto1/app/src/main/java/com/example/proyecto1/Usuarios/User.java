package com.example.proyecto1.Usuarios;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class User extends AppCompatActivity implements RespuestaHilo {

    TextView mTextview;
    Map<String, String> mapa = new LinkedHashMap<>();
    ProcesarPeticiones pet = new ProcesarPeticiones();

/*Este será la clase para un usuario que hayamos elegido desde la lista de usuarios. Contendrá los
mismos datos que nuestro propio usuario: Nombre, apellidos, email y tipo de usuario.
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Bundle s = getIntent().getExtras();
        String email = "\nEmail:\n" + s.getString("email");
        String tipo = "\nTipo de usuario:\n" + estado(s.getString("user_type"));

        mTextview = (TextView) findViewById(R.id.user);

        mTextview.setText(s.getString("user"));

        mTextview = (TextView) findViewById(R.id.email);

        mTextview.setText(email);

        mTextview = (TextView) findViewById(R.id.tipo_usuario);

        mTextview.setText(tipo);

    }


    //Este método devuelve el tipo de usuario que eres según tu número, para que cualquiera pueda entenderlo
    public String estado(String s) {
        switch (s) {
            case "1":
                s = "Usuario sin login";
                return s;


            case "2":
                s = "Cliente";
                return s;

            case "3":
                s = "Tecnico";
                return s;

            case "4":
                s = "Supervisor";
                return s;

            case "5":
                s = "Administrador";
                return s;

        }
        return s;
    }

    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        Log.d("PRUEBA", "Estos son los usuarios");
        if (respuesta.getInt("response") == 200) {
            Log.e("ID", String.valueOf(ClienteTFG.contenido));
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("Etiquetas", content.toString());
        } else {

        }
    }
}
