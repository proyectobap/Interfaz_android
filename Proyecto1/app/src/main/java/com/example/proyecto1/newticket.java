package com.example.proyecto1;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.proyecto1.ClientMethods.ClienteTFG;

import org.json.JSONObject;

public class newticket extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newticket);
    }

    public void nuevoticket(View v) {

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        TextInputEditText titulo=  findViewById(R.id.titulo);
        TextInputEditText descripcion= findViewById(R.id.descripcion);

        JSONObject respuesta= ClienteTFG.crearTicket(titulo.getText().toString(), descripcion.getText().toString());
        ClienteTFG.probando(respuesta);

        }

    public void desconectar(View v){

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ClienteTFG.desconectar();
        System.exit(0);
    }


}