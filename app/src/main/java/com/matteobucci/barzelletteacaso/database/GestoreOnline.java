package com.matteobucci.barzelletteacaso.database;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.matteobucci.barzelletteacaso.StatStr;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.TabellaBarzellette;
import com.matteobucci.barzelletteacaso.model.Tipo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matteo on 30/01/16.
 */
public class GestoreOnline {




    private String tabellaDatabase = null;
    private boolean noAdulti = false;
    private boolean noLunga = false;
    private boolean consigliate = false;
    private Tipo tipoRichiesta = null;
    private Categoria categoriaRichiesta = null;
    private Context context;
    private ResultBarzelletteListener listenerBarzellette = null;
    private ResultTabelleListener listenerTabelle = null;

    public GestoreOnline(Context context, ResultBarzelletteListener listenerBarzellette,ResultTabelleListener listenerTabelle, boolean noAdulti, boolean noLunga, boolean consigliate){
        this.noAdulti = noAdulti;
        this.noLunga = noLunga;
        this.consigliate = consigliate;
        this.context = context;
        this.listenerBarzellette = listenerBarzellette;
        this.listenerTabelle = listenerTabelle;


    }

    public GestoreOnline(Context context, ResultBarzelletteListener listenerBarzellette){
        this(context, listenerBarzellette, null,false, false, false);
    }

    public GestoreOnline(Context context, ResultBarzelletteListener listenerBarzellette, ResultTabelleListener listenerTabelle){
        this(context, listenerBarzellette, listenerTabelle,false, false, false);
    }

    public interface ResultBarzelletteListener {
        public void onResult(List<Barzelletta> listaBarzellette);
    }

    public interface ResultTabelleListener {
        public void onResult(List<TabellaBarzellette> listaTabelle);
    }


    public String getTabellaDatabase() {
        return tabellaDatabase;
    }

    public void setTabellaDatabase(String tabellaDatabase) {
        this.tabellaDatabase = tabellaDatabase;
    }


    public Categoria getCategoriaRichiesta() { return categoriaRichiesta; }

    public void setCategoriaRichiesta(Categoria categoriaRichiesta){this.categoriaRichiesta = categoriaRichiesta;}

    public boolean isNoAdulti() {
        return noAdulti;
    }

    public void setNoAdulti(boolean noAdulti) {
        this.noAdulti = noAdulti;
    }

    public boolean isNoLunga() {
        return noLunga;
    }

    public void setNoLunga(boolean noLunga) {
        this.noLunga = noLunga;
    }

    public boolean isConsigliate() {
        return consigliate;
    }

    public void setConsigliate(boolean consigliate) {
        this.consigliate = consigliate;
    }

    public Tipo getTipoRichiesta() {
        return tipoRichiesta;
    }

    public void setTipoRichiesta(Tipo tipoRichiesta) {
        this.tipoRichiesta = tipoRichiesta;
    }

    public void getBarzellette(int daID, int aID){
        //http://localhost/jsonString.php?id_superiore=100&id_inferiore=10&id_categoria=2&id_lunghe=1&id_adulti=0&id_tipo=%27testo%27 Esempio di stringa

        String url = StatStr.NOMEHOST;
        url = url + "/jsonString.php?";
        if(getTipoRichiesta() != null){
            url += "id_tipo=\'" + getTipoRichiesta().toString().toLowerCase() + "\'&";
        }
        if(getCategoriaRichiesta()!=null){
            url += "id_categoria=" + getCategoriaRichiesta().getID() + "&";
        }
        if(getTabellaDatabase()!=null){
            url += "tabella_database=" + getTabellaDatabase() + "&";
        }

        if(isNoAdulti()){
            url += "id_adulti=0&";
        }
        if(isNoLunga()){
            url += "id_lunghe=0&";
        }
        if(daID != 0){
            url += "id_inferiore=" + daID +"&";
        }
        if(aID != 0){
            url += "id_superiore=" + daID +"&"  ;
        }



        Log.i("CAZZO", url);

        // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("JSON Arrivato", response);
                                if(listenerBarzellette !=null) {
                                   listenerBarzellette.onResult(parseString(response));
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("GestoreOnline", "errore nella risposta");
                        listenerBarzellette.onResult(null);
                    }
                });
        // Add the request to the RequestQueue.
                queue.add(stringRequest);
    }

        private List<Barzelletta> parseString(String response){
            List<Barzelletta> result = new ArrayList<Barzelletta>();

            int id;
            String testo;
            Categoria categoria;
            boolean adulti;
            boolean lunga;
            Tipo tipo;


            try {
                JSONObject risultato = new JSONObject(response);
                JSONArray arrayBarzellette = risultato.getJSONArray("barzellette");

                for(int i=0; i<arrayBarzellette.length(); i++){
                    JSONObject json_data = arrayBarzellette.getJSONObject(i);

                    id = json_data.getInt(StatStr.JSON_ID);
                    testo = json_data.getString(StatStr.JSON_TESTO);
                    try {
                        categoria = Categoria.getCategoria(json_data.getInt(StatStr.JSON_CATEGORIA));
                    } catch (NumberFormatException e) {
                        categoria = Categoria.UNDEFINED;
                    }

                    adulti = json_data.getInt(StatStr.JSON_ADULTI) != 0;
                    lunga =  json_data.getInt(StatStr.JSON_LUNGA) != 0;

                    String stringTipo = json_data.getString(StatStr.JSON_TIPO);
                    if(stringTipo.equals("testo")){
                        tipo = Tipo.TESTO;
                    }
                    else if(stringTipo.equals("immagine")){
                        tipo = Tipo.IMMAGINE;
                    }
                    else{
                        tipo = Tipo.TESTO;
                    }

                   result.add(new Barzelletta(id, testo, adulti, categoria, lunga, tipo));

                }

            } catch (JSONException e) {
                e.printStackTrace();
                return result = null;
            }


            return result;
        }



    public void getTabelle(){

        String url = StatStr.NOMEHOST;
        url = url + "/elenco_tabelle2.php?";

        url+= "application_name=" + "com.matteobucci.barzelletteacaso&";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("JSON Arrivato", response);
                        if(listenerTabelle !=null) {
                            listenerTabelle.onResult(parseStringTabelle(response));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage() != null) {
                    Log.e("GestoreOnline", error.getMessage());
                }
                listenerTabelle.onResult(null);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);



    }


    private List<TabellaBarzellette> parseStringTabelle(String response){
        List<TabellaBarzellette> result = new ArrayList<TabellaBarzellette>();

        int id;
        String nome;
        int numero;
        String descrizione;
        String nomeVisualizzato;



        try {
            JSONObject risultato = new JSONObject(response);
            JSONArray arrayBarzellette = risultato.getJSONArray(StatStr.JSON_ELENCO_TABELLE);

            for(int i=0; i<arrayBarzellette.length(); i++){
                JSONObject json_data = arrayBarzellette.getJSONObject(i);



                    id = json_data.getInt(StatStr.JSON_ID_TABELLA);
                    nome = json_data.getString(StatStr.JSON_NOME_TABELLA);
                    numero = json_data.getInt(StatStr.JSON_ELEMENTI_TABELLA);
                    nomeVisualizzato = json_data.getString(StatStr.JSON_NOME_VISUALIZZATO_TABELLA);
                    descrizione = json_data.getString(StatStr.JSON_DESCRIZIONE_TABELLA);


                    result.add(new TabellaBarzellette(nome, id, numero, descrizione, nomeVisualizzato));


            }

        } catch (JSONException e) {
            e.printStackTrace();
            return result = null;
        }


        return result;
    }





}




