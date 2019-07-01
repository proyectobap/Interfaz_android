package com.example.proyecto1.ClientMethods;

import android.util.Log;

import com.example.proyecto1.Loading;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClienteTFG implements Runnable {


    private String respuestaEnc;
    private EncryptModule enc;

    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private Socket servidor;


    private JSONObject respuesta;
    public static JSONArray contenido;
    private JSONObject peticion;

    private RespuestaHilo interfazRespuesta;

    private String userName = "";
    private String passWord = "";
    private String token;
    private ConexionHilo interfaz;
    private final Thread conexion;


    public ClienteTFG(String userName, String passWord, EncryptModule enc, ConexionHilo interfaz) {
        this.userName = userName;
        this.passWord = passWord;
        this.enc = enc;
        this.interfaz = interfaz;
        conexion = new Thread(this, "conexion");
    }



    public Thread getHilo() {
        return conexion;
    }

    public void setInstruccion(JSONObject peticion, RespuestaHilo interfaz) {
        this.peticion = peticion;
        this.interfazRespuesta = interfaz;
        this.conexion.interrupt();
    }

    @Override
    public void run() {

        /***********************************************************************************/
        /* Inicializa las claves de cifrado asimétrico y abre una conexión con el servidor */
        /***********************************************************************************/

        try {

            enc.setSecureRandom(new SecureRandom());
            enc.initializeKey();
            servidor = new Socket("proyectobap.ddns.net",35698);
            salida = new ObjectOutputStream(servidor.getOutputStream());
            entrada = new ObjectInputStream(servidor.getInputStream());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        /******************************************************************************/
        /* Intercambia las claves con el servidor y hace una prueba de conexión para  */
        /* comprobar la validez de las claves                                         */
        /******************************************************************************/
        try {

            intercambioClaves();
            enc.setCifradorAsimetrico(Cipher.getInstance("RSA/ECB/PKCS1Padding"));
            enc.setCifradorSimetrico(Cipher.getInstance("AES/GCM/NoPadding"));

            String check = enc.asymetricDecript((String) entrada.readObject());
            enviar(enc.asymetricEncrypt(check));

            if (entrada.readInt() == 206) {
                System.out.println("Comunicación OK");
                System.out.print("Recibiendo clave simetrica... ");
                String claveVolatil = (String)entrada.readObject();
                enc.setClaveSimetrica(Base64.getDecoder().decode(claveVolatil.getBytes()));
                enc.inicializacionClaveSimetrica();
                System.out.println("OK");
            } else {
                System.err.println("Comunicación falló");
                System.exit(0);
            }

        } catch (Exception e) {
            // Controlar
        }

        /******************************************************************************/
        /* Envía al servidor las credenciales para obtener un token de conexión si    */
        /* fuesen validas                                                             */
        /******************************************************************************/

        try {

            enviar(enc.symetricEncrypt(userName+","+passWord));

            respuestaEnc = (String) entrada.readObject();
            respuesta = new JSONObject(enc.symetricDecript(respuestaEnc));
            contenido = new JSONArray();

            /*
             * Si las credenciales son válidas, guarda el token facilitado por el servidor
             * y continua
             */

            if (respuesta.getInt("response") == 200) {
                contenido = respuesta.getJSONArray("content");
                interfaz.response(true);
            } else {
                interfaz.response(false);
                this.cerrarConexion();
            }

        } catch (Exception e) {
            // controlar
        }

        /******************************************************************************/

        boolean running = true;

        while (running) {

            try {
                Thread.sleep(900000);
            } catch (InterruptedException e) {
                try {
                    // Esta petición devuelve un JSONObject con la información requerida
                    // Enviadla donde necesitéis para continuar con el programa
                    Log.e("PETICION", peticion.toString());
                    if (peticion.getString("peticion").equalsIgnoreCase("exit")) {
                        running = false;
                    }
                    peticion(peticion);

                } catch (Exception e1) {
                    Log.e("ERROR", "error");
                }
                continue;
            }
            System.out.println("Cerrando conexión...");
            break;

        }

        /******************************************************************************/

        try {
            cerrarConexion();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /******************************************************************************/

    public void cerrarConexion() throws Exception {
        servidor.close();
        salida.close();
        entrada.close();
    }

    /******************************************************************************/

    private void peticion(JSONObject peticion) throws Exception {
        System.out.println(peticion.toString());
        enviar(enc.symetricEncrypt(peticion.toString()));

        if (peticion.getString("peticion").equalsIgnoreCase("exit")) {
            System.out.println("Desconectando...");
            this.cerrarConexion();
            System.exit(0);
        }

        respuestaEnc = (String) entrada.readObject();
        respuesta= new JSONObject(enc.symetricDecript(respuestaEnc));
        interfazRespuesta.respuesta(respuesta);



    }

    /******************************************************************************/

    private void intercambioClaves() throws
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            IOException,
            ClassNotFoundException {

        System.out.print("Enviando clave publica propia... ");
        salida.writeObject(enc.getPublicKeySpec().getModulus());
        salida.flush();
        salida.writeObject(enc.getPublicKeySpec().getPublicExponent());
        salida.flush();
        System.out.println("OK");

        System.out.print("Recibiendo clave publica del servidor... ");
        BigInteger modulo = (BigInteger) entrada.readObject();
        BigInteger exponente = (BigInteger) entrada.readObject();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        enc.setServerPublicKey(keyFactory.generatePublic(new RSAPublicKeySpec(modulo,exponente)));
        System.out.println("OK");

    }

    /******************************************************************************/

    private void enviar(Object mensaje) throws IOException {
        salida.writeObject(mensaje);
        salida.flush();
    }

    /******************************************************************************/

    public String getToken() {
        return token;
    }

    /******************************************************************************/

    public static JSONObject crearTicket(String titulo, String descripcion) {
        JSONObject prueba = new JSONObject();
        try {
            prueba.put("peticion","newTicket");
            prueba.put("title", titulo);
            prueba.put("description", descripcion);
            prueba.put("status", 2);
            prueba.put("owner", 2);
            prueba.put("object", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return prueba;

    }

}