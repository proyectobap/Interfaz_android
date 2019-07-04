package com.example.proyecto1.ClientMethods;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Map;

//Este método nos servirá para conseguir el map que enviemos. Cogerá todos los valores y llaves y creará un JSON para mandar peticiones
public class ProcesarPeticiones {
    public JSONObject peticiones(Map<String,String> mapa){
        JSONObject pregunta= new JSONObject();
        try {
            for (Map.Entry<String, String> p: mapa.entrySet()){
                String clave= p.getKey();
                String valor= p.getValue();
                pregunta.put(clave, valor);
                }
            return pregunta;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pregunta;
        }


    }

