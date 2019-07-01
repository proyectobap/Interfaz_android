package com.example.proyecto1.ClientMethods;

import android.app.Application;

public class Informacion extends Application {

    private static ClienteTFG conexion;
    private static EncryptModule enc;

    public static ClienteTFG getConexion() {
        return conexion;
    }

    public static void setConexion(ClienteTFG conexion) {
        Informacion.conexion = conexion;
    }

    public static void iniciarConexion(String user, String password, ConexionHilo interfaz) {
        enc = new EncryptModule();
        conexion = new ClienteTFG(user, password, enc, interfaz);
        conexion.getHilo().start();
    }

}
