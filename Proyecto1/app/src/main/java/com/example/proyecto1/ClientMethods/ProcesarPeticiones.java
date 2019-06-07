package com.example.proyecto1.ClientMethods;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Map;


public class ProcesarPeticiones {
    public JSONObject peticiones(Map<String,String> mapa){
        JSONObject pregunta= new JSONObject();
        System.out.println(mapa.entrySet());
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

