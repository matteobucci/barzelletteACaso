package com.matteobucci.barzelletteacaso.model;

import android.content.Context;
import android.util.Log;

import com.parse.ParseAnalytics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matti on 23/06/2015.
 */

public class Favorite {

    private static String FAVORITE_FILENAME = "favoriti.dat";
    private final String TAG = this.getClass().getSimpleName();

    private static Favorite instance = null;

    private List<Barzelletta> favoriteList;
    private Context context;


    private Favorite(Context context){
        this.context = context;
        favoriteList = new ArrayList<>();
        Log.i(TAG, "Favoriti creati");
    }

    public static Favorite getInstance(Context context){
        if(instance == null){
            instance = new Favorite(context);
        }
        else{
            Log.i("Favorite", "Istanza già presente");
        }
        return instance;
    }

   public int size(){
       return favoriteList.size();
   }

    public boolean isEmpty(){
        return favoriteList.isEmpty();
    }

    public boolean loadFavorite(){

        try {
            ObjectInputStream oop = new ObjectInputStream(context.openFileInput(FAVORITE_FILENAME));
            favoriteList = (List<Barzelletta>) oop.readObject();
            oop.close();

        }  catch (IOException e) {
            Log.e(TAG, "Errore nella lettura del file in memoria: impossibile leggere il file");
            return false;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Errore nella lettura del file in memoria: trovata una classe diversa da quella prevista");
            return false;
        }

        if(favoriteList!= null) {
            Log.i(TAG, "Favoriti caricati, sembra funzionare tutto");
            return true;
        }
        else return false;
    }

    public boolean saveFavorite(){

        try {

            ObjectOutputStream oip = new ObjectOutputStream(context.openFileOutput(FAVORITE_FILENAME, Context.MODE_PRIVATE));
            oip.writeObject(favoriteList);
            oip.close();
        } catch (IOException e) {
            Log.e(TAG, "Impossibile aprire o scrivere nel file");
            return false;
        }

        Log.i(TAG, "Favoriti salvati, sembra funzionare tutto");
        return true;
    }

    public List<Barzelletta> getFavoriteList(){
        return favoriteList;
    }

    public List<Barzelletta> getFavoriteTesto(){
        List<Barzelletta> result = new ArrayList<>();
        for(Barzelletta questa: favoriteList){
            if(questa.getTipo().equals(Tipo.TESTO))
                result.add(questa);
        }
        return result;
    }

    public List<Barzelletta> getFavoriteImmagine(){
        List<Barzelletta> result = new ArrayList<>();
        for(Barzelletta questa: favoriteList){
            if(questa.getTipo().equals(Tipo.IMMAGINE))
                result.add(questa);
        }
        return result;
    }


    public void add(Barzelletta questa){
        favoriteList.add(questa);

        //Segnalo a parse l'aggiunta del preferito
        Map<String, String> dimensions = new HashMap<String, String>();
        dimensions.put("id_barzelletta", Integer.toString(questa.getID()));
        ParseAnalytics.trackEventInBackground("favorite", dimensions);

        Log.i(TAG, "Barzelletta aggiunta");
    }

    public void remove(Barzelletta questa){
        favoriteList.remove(questa);
        Log.i(TAG, "Barzelletta rimossa");
    }



    public boolean contains (Barzelletta questa){
        return favoriteList.contains(questa);
    }



    public void removeByID(int id){
        Barzelletta daTrovare = null;
        for(Barzelletta questa: favoriteList){
            if(questa.getID() == id){
                daTrovare = questa;
            }
        }
        if(daTrovare!=null) {
            this.remove(daTrovare);
        }
    }

    public int getNumeroFavoriti(){
        return favoriteList.size();
    }


}
