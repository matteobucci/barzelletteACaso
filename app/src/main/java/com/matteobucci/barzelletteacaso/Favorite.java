package com.matteobucci.barzelletteacaso;

import android.content.Context;
import android.util.Log;

import com.matteobucci.barzelletteacaso.model.Barzelletta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matti on 23/06/2015.
 */
public class Favorite {

    private static String FAVORITE_FILENAME = "favoriti.dat";


    private static Favorite instance = null;

    private List<Barzelletta> favoriteList;
    private Context context;


    private Favorite(Context context){
        this.context = context;
        favoriteList = new ArrayList<>();
        Log.i("Favorite", "Favoriti creati");
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
            Log.e("PERSISTENCE", "Errore nella lettura del file in memoria: impossibile leggere il file");
            return false;
        } catch (ClassNotFoundException e) {
            Log.e("PERSISTENCE", "Errore nella lettura del file in memoria: trovata una classe diversa da quella prevista");
            return false;
        }

        if(favoriteList!= null) {
            Log.i("PERSISTENCE", "Favoriti caricati, sembra funzionare tutto");
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
            Log.e("PERSISTENCE", "Impossibile aprire o scrivere nel file");
            return false;
        }

        Log.i("PERSISTENCE", "Favoriti salvati, sembra funzionare tutto");
        return true;
    }

    public List<Barzelletta> getFavoriteList(){
        return favoriteList;
    }

    public void add(Barzelletta questa){
        favoriteList.add(questa);
        Log.i("Favorite", "Barzelletta aggiunta");
    }

    public void remove(Barzelletta questa){
        favoriteList.remove(questa);
        Log.i("Favorite", "Barzelletta rimossa");
    }

    public boolean contains (Barzelletta questa){
        return favoriteList.contains(questa);
    }






}
