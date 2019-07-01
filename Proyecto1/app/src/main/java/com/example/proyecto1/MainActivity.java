package com.example.proyecto1;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;

import android.view.View;



import com.example.proyecto1.ClientMethods.ClienteTFG;
import com.example.proyecto1.ClientMethods.ConexionHilo;
import com.example.proyecto1.ClientMethods.EncryptModule;
import com.example.proyecto1.ClientMethods.Informacion;


public class MainActivity extends AppCompatActivity implements ConexionHilo {
    public ClienteTFG clienteTFG;
    EncryptModule enc;

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
        TextInputEditText contrasenia= findViewById(R.id.caja2);
        Informacion.iniciarConexion(usuario.getText().toString(), contrasenia.getText().toString(), this);

        }


    @Override
    public void response(boolean b) {
        if (b) {
            Intent intent= new Intent(this, Loading.class);
            startActivity(intent);
        } else {

            Informacion.setConexion(null);
        }
    }
}


