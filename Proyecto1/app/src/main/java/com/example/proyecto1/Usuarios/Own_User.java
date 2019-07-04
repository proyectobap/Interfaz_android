package com.example.proyecto1.Usuarios;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Loading;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Own_User extends Fragment implements RespuestaHilo {

    TextView mTextview;
    Map<String, String> mapa = new LinkedHashMap<>();
    ProcesarPeticiones pet = new ProcesarPeticiones();
    TextInputEditText pass;
/*Este método sirve para ver tu propio user. Lo conseguiremos de la petición hecha de todos los usuarios,
y al encontrar tu id (conseguido por loading) cogerá esos datos y los pondrá en esta view. También podremos
cambiar la contraseña poniendo una nueva y dándole al botón puesto para ello.
 */
    @Override
    public View  onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_own__user, container, false);
        pass = view.findViewById(R.id.new_password);

        Button button = (Button) view.findViewById(R.id.boton_nueva_contraseña);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changePassword();
            }
        });



        String email= "\nEmail:\n" + Loading.own_email;
        String tipo = "\nTipo de usuario:\n" + estado(Loading.own_type);

        mTextview = (TextView)view.findViewById(R.id.own_user);

        mTextview.setText(Loading.own_user);

        mTextview = (TextView)view.findViewById(R.id.own_email);

        mTextview.setText(email);

        mTextview = (TextView)view.findViewById(R.id.own_tipo_usuario);

        mTextview.setText(tipo);

        return view;
    }

//Este método coge la información puesta en la caja de la password y cambia la contraseña de nuestro user
    public void changePassword(){
        mapa.put("peticion", "modifyownpassword");
        mapa.put("shdw_passwd", pass.getText().toString());
        JSONObject tickets = pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
        Toast toast = Toast.makeText(getContext(), "Contraseña modificada", Toast.LENGTH_LONG);
        toast.show();
    }

//Este método devuelve el tipo de usuario que eres según tu número, para que cualquiera pueda entenderlo
    public String estado(String s){
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
