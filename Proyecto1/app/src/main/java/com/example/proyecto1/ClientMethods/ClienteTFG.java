package com.example.proyecto1.ClientMethods;

import android.support.v7.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ClienteTFG extends AppCompatActivity {


    private static String responder;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;
    private static RSAPublicKeySpec publicKeySpec;

    private static SecretKey claveSimetricaSecreta;
    private static SecureRandom secureRandom;
    private static byte[] claveSimetrica;
    private static String respuestaEnc;

    private static GCMParameterSpec parameterSpec;

    private static PublicKey serverPublicKey;
    private static Cipher cifradorAsimetrico;
    private static Cipher cifradorSimetrico;

    private static ObjectOutputStream salida;
    private static ObjectInputStream entrada;
    private static Socket servidor;

    private static boolean running;
    private static boolean pruebaConexion;
    private static JSONObject pregunta;
    private static JSONObject respuesta;
    private static JSONArray content;
    private String userName = "";
    private String passWord = "";
    private String token;

    private static ClienteTFG cliente;

    public ClienteTFG(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;

    }


    public static ClienteTFG getCliente() {
        return cliente;
    }



    public void iniciarConexion() {

        if (this.userName.equals("") || this.passWord.equals("")) {
            System.out.println("No pueden quedar campos vacíos!");
            return;
        }

        try {

            /*
             * Inicializa las claves de cifrado asimétrico y abre una conexión con el servidor
             */

            secureRandom = new SecureRandom();
            initializeKey();
            servidor = new Socket("proyectobap.ddns.net",35698);
            salida = new ObjectOutputStream(servidor.getOutputStream());
            entrada = new ObjectInputStream(servidor.getInputStream());


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }



        try {

            /*
             * Intercambia las claves con el servidor y hace una prueba de conexión para
             * comprobar la validez de las claves
             */

            intercambioClaves();
            cifradorAsimetrico = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cifradorSimetrico = Cipher.getInstance("AES/GCM/NoPadding");

            String check = asymetricDecript((String) entrada.readObject());
            enviar(asymetricEncrypt(check));

            if (entrada.readInt() == 206) {
                System.out.println("Comunicación OK");
                System.out.print("Recibiendo clave simetrica... ");
                String claveVolatil = (String)entrada.readObject();
                claveSimetrica = Base64.getDecoder().decode(claveVolatil.getBytes());
                inicializacionClaveSimetrica();
                System.out.println("OK");
            } else {
                System.err.println("Comunicación falló");
                System.exit(0);
            }

            /*
             * Envía al servidor las credenciales para obtener un token de conexión
             * si fuesen validas
             */

            enviar(symetricEncrypt(userName+","+passWord));

            respuestaEnc = (String) entrada.readObject();
            respuesta = new JSONObject(symetricDecript(respuestaEnc));
            content = new JSONArray();

            /*
             * Si las credenciales son válidas, guarda el token facilitado por el servidor
             * y continua
             */

            if (respuesta.getInt("response") == 200) {
                content = respuesta.getJSONArray("content");
                token = content.getJSONObject(0).getString("content");
            } else {
                System.err.println("Login incorrecto");
                this.desconectar();
            }

        } catch (UnknownHostException e) {
            System.out.println("No se puede establecer comunicación con el servidor");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de E/S");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClienteTFG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ClienteTFG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




    /******************************************************************************/
    public static JSONArray probando(JSONObject pregunta)  {


        try {
            System.out.println(pregunta.toString());
            enviar(symetricEncrypt(pregunta.toString()));

            if (pregunta.getString("peticion").equalsIgnoreCase("exit")) {
                System.out.println("Desconectando...");
                System.exit(0);
            }

            System.out.print("Código respuesta: ... ");
            respuestaEnc = (String) entrada.readObject();
            respuesta = new JSONObject(symetricDecript(respuestaEnc));
            content = new JSONArray();

            System.out.println(respuesta.getInt("response"));

            content = respuesta.getJSONArray("content");


            return content;
        } catch (IOException | JSONException | ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (InvalidAlgorithmParameterException e1) {
            e1.printStackTrace();
        } catch (IllegalBlockSizeException e1) {
            e1.printStackTrace();
        } catch (BadPaddingException e1) {
            e1.printStackTrace();
        }
        return content;


    }



    /******************************************************************************/

    public static void desconectar() {
        try {
            entrada.close();
            salida.close();
            servidor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /******************************************************************************/

    private static void inicializacionClaveSimetrica() {
        claveSimetricaSecreta = new SecretKeySpec(claveSimetrica, "AES");
    }

    /******************************************************************************/

    public static void initializeKey() throws Exception {
        System.out.print("Iniciando generador de claves... ");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        System.out.println("OK");

        System.out.print("Generando par de claves... ");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        System.out.println("OK");

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        System.out.print("Generando clave publica exportable... ");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
        System.out.println("OK");
    }

    /******************************************************************************/

    private static void intercambioClaves() throws
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            IOException,
            ClassNotFoundException {

        System.out.print("Enviando clave publica propia... ");
        salida.writeObject(publicKeySpec.getModulus());
        salida.flush();
        salida.writeObject(publicKeySpec.getPublicExponent());
        salida.flush();
        System.out.println("OK");

        System.out.print("Recibiendo clave publica del servidor... ");
        BigInteger modulo = (BigInteger) entrada.readObject();
        BigInteger exponente = (BigInteger) entrada.readObject();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        serverPublicKey = keyFactory.generatePublic(new RSAPublicKeySpec(modulo, exponente));
        System.out.println("OK");
    }

    /******************************************************************************/

    private static String symetricEncrypt(String mensaje) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
        parameterSpec = new GCMParameterSpec(128, iv);
        cifradorSimetrico.init(Cipher.ENCRYPT_MODE, claveSimetricaSecreta, parameterSpec);

        byte[] cipherText = cifradorSimetrico.doFinal(mensaje.getBytes());
        ByteBuffer bf = ByteBuffer.allocate(4 + iv.length + cipherText.length);
        bf.putInt(iv.length);
        bf.put(iv);
        bf.put(cipherText);

        byte[] cipherMessage = bf.array();
        return new String(Base64.getEncoder().encode(cipherMessage));
    }

    /******************************************************************************/

    private static String symetricDecript(String mensajeCifrado64) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] cifMen = Base64.getDecoder().decode(mensajeCifrado64);
        ByteBuffer bf = ByteBuffer.wrap(cifMen);
        int ivLength = bf.getInt();
        if (ivLength < 12 || ivLength >= 16) {
            throw new IllegalArgumentException("invalid iv length");
        }
        byte[] iv = new byte[ivLength];
        bf.get(iv);
        byte[] cipherText = new byte[bf.remaining()];
        bf.get(cipherText);

        parameterSpec = new GCMParameterSpec(128, iv);
        cifradorSimetrico.init(Cipher.DECRYPT_MODE, claveSimetricaSecreta, parameterSpec);
        return new String(cifradorSimetrico.doFinal(cipherText));
    }

    /******************************************************************************/

    private static String asymetricEncrypt(String mensaje) throws
            UnsupportedEncodingException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException {

        cifradorAsimetrico.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        byte[] mensajeCifrado = cifradorAsimetrico.doFinal(mensaje.getBytes("UTF8"));
        return new String(Base64.getEncoder().encode(mensajeCifrado));
    }

    /******************************************************************************/

    private static String asymetricDecript(String mensajeCifrado64) throws
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException {

        byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeCifrado64);
        cifradorAsimetrico.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cifradorAsimetrico.doFinal(mensajeCifrado));
    }

    /******************************************************************************/

    private static void enviar(Object mensaje) throws IOException {
        salida.writeObject(mensaje);
        salida.flush();
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
