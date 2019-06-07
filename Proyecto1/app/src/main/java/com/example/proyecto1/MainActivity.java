package com.example.proyecto1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.Toast;

import com.example.proyecto1.ClientMethods.ClienteTFG;



public class MainActivity extends AppCompatActivity {
    ClienteTFG clienteTFG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void login(View v) {

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        TextInputEditText usuario=  findViewById(R.id.caja1);
        TextInputEditText contraseña= findViewById(R.id.caja2);
        clienteTFG= new ClienteTFG(usuario.getText().toString(), contraseña.getText().toString());
        clienteTFG.iniciarConexion();
            try {
                wait(2000);
            } catch (Exception e) {

            }
            Intent intent = new Intent (this,ConexionAceptada.class);
            startActivity(intent);
        }









}


