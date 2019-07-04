package com.example.proyecto1.lista;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.proyecto1.ClientMethods.Informacion;
import com.example.proyecto1.ClientMethods.ProcesarPeticiones;
import com.example.proyecto1.ClientMethods.RespuestaHilo;
import com.example.proyecto1.Contenedor_tickets;
import com.example.proyecto1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.proyecto1.lista.ticket.id_ticket;


public class Elements extends Fragment implements RespuestaHilo {
    TextView mTextview;
    Contenedor_tickets c;
    ArrayList<String> elementos= new ArrayList<>();
    Map<String,String> mapa= new LinkedHashMap<>();
    ProcesarPeticiones pet= new ProcesarPeticiones();
    ArrayList<String> ids= new ArrayList<>();
    View view;

    public Elements(){

    }
/*En esta clase llamaremos al elemento asignado al ticket en cuestión.
El handler será para retrasar un poco la petición, ya que pueden solaparse las peticiones de los
eventos y los detalles del ticket
 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_elements, container, false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                peticionElementos();
            }
        }, 1500);


        mTextview = (TextView)view.findViewById(R.id.elementos);

        initializeUI();

        return view;

    }
/*Este método llama a RunOnUiThread para que el hilo que no es principal pueda (metiendo lo que haya
dentro del método al propio hilo principal) modificar la interfaz, ya que puede dar error.
 */
    public void initializeUI() {
        getActivity().runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (elementos.size() == 0) {
                    mTextview.setText("Sin elementos");
                } else {
                    mTextview.setText(elementos.get(0));
                }

            }
        });
    }
//Con este método pedimos el elemento asociado al ticket
    public void peticionElementos(){
        mapa.clear();
        mapa.put("peticion","ELEMENTRELATION");
        mapa.put("ticket_id", id_ticket);
        JSONObject tickets=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(tickets, this);
    }
/*Este método recogerá el elemento si es de tipo hardware y luego ajustará la interfaz con el método
    para que aparezca
 */
    public void conseguirElementoHardware(JSONArray listado) throws Exception {
        elementos.clear();

        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            elementos.add("Nombre:\n" + rec.getString("internal_name") + "\nS/N:\n" +
                    rec.getString("S/N") + "\nBrand:\n" + rec.getString("brand") +
                    "\nModel:\n" + rec.getString("model"));

        }

        initializeUI();

    }
    /*Este método recogerá el elemento si es de tipo software y luego ajustará la interfaz con el método
        para que aparezca
     */
    public void conseguirElementoSoftware(JSONArray listado) throws Exception {
        elementos.clear();

        for (int i = 0; i < listado.length(); ++i) {
            JSONObject rec = listado.getJSONObject(i);
            elementos.add("Nombre:\n" + rec.getString("internal_name") + "\nDeveloper:\n" +
                    rec.getString("developer") + "\nVersion:\n" + rec.getString("version"));
        }

        initializeUI();

    }
//Con este método los detalles del elemento asociado al ticket (ya que el anterior solo nos daba el id)
    public void peticionElemento(String ids){
        mapa.clear();
        mapa.put("peticion","ELEMENTDETAILS");
        mapa.put("element_id", ids);
        JSONObject elemento=pet.peticiones(mapa);
        Informacion.getConexion().setInstruccion(elemento, this);
    }

   /*En la respuesta comprobaremos si el elemento relacionado es un hardware o un software.
   Si es un software, la respuesta de la petición contendrá "developer", y si es hardware contendrá
   "brand"
    */
    @Override
    public void respuesta(JSONObject respuesta) throws Exception {
        // Recoger respuesta del servidor y procesarla
        if (respuesta.getInt("response") == 200) {
            JSONArray content = respuesta.getJSONArray("content");
            Log.e("peticion", content.toString());
            if (content.getJSONObject(0).has("brand")){
                conseguirElementoHardware(content);
                Log.e("hola","paso por aqui");
            }
            if (content.getJSONObject(0).has("developer")){
                conseguirElementoSoftware(content);
            } else {
                ids.add(content.getJSONObject(0).getString("element_id"));
                peticionElemento(ids.get(0));
                }
            }

         else {

        }
    }
}
