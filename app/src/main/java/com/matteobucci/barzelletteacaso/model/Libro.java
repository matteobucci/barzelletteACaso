package com.matteobucci.barzelletteacaso.model;

import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Matti on 23/06/2015.
 */
public class Libro {

    static final String TAG = Libro.class.getSimpleName();

    //Variabili per Parse
    private static final String SESSIONE_BARZELLETTE_OBJECT_KEY = "NuovaSessioneBarzellette";
    private static final String NUMERO_BARZELLETTE_KEY = "numeroBarzellette";
    private static final String CATEGORIA_KEY = "categoriaSessione";
    private static final String TUTTE_LE_CATEGORIE = "tutte";
    private int barzelletteLette = 0;

    //Variabili per il funzionamento
    private Random random;
    private Map<Integer, Barzelletta> mappa;
    private List<Integer> keys;
    private LinkedList<Integer> ultimiIndici = new LinkedList<Integer>();
    private int size;
    //private int lastIndex;

    private static int BARZELLETTE_LASCIATE = 5; //Sceglie le barzellette tra quelle che non sono ancora uscite tra tutte quelle che ci sono meno questo numero

    public Libro(){
        mappa = new HashMap<>();
        random = new Random();
    }

    public Libro(Collection<Barzelletta> listaBarzellette){
        this();

        for(Barzelletta attuale: listaBarzellette) {
            mappa.put(attuale.getID(), attuale);
            }

        if(mappa.isEmpty()){
            mappa.put(-1, new Barzelletta(-1, "Non ci sono ancora barzellette in questa categoria"));
        }

        keys = new ArrayList<>(mappa.keySet());
        size = keys.size();
        Log.i(TAG, "Libro creato con " + size + " barzellette.");
    }

    public Barzelletta getByID(int id){
        //lastIndex = id;
        return mappa.get(id);
    }

    public Barzelletta getRandom(){
        int numero;
        barzelletteLette++;
        do {
            numero = random.nextInt(size);
        }while(!checkLast(numero));

        Log.i(TAG, numero + "");



        return mappa.get(keys.get(numero));

    }

    private boolean checkLast(int index){
        if(ultimiIndici.contains(index))
            return false;
        else{
            ultimiIndici.addLast(index);
            if(ultimiIndici.size()>size-BARZELLETTE_LASCIATE){
                ultimiIndici.removeFirst();
            }
            return true;
        }
    }

    public int size(){
        return size;
    }

    public String toString(){
        return "Libro:\n" + size + " elementi";
    }

    public Libro filterBy(Categoria categoria){
        List<Barzelletta> result = new ArrayList<>();
        for(Integer key : keys){
            if(mappa.get(key).getCategoria().equals(categoria))
                result.add(mappa.get(key));
        }

        return new Libro(result);
    }

    public Libro filterBy(boolean adulti){
        List<Barzelletta> result = new ArrayList<>();
        for(Integer key : keys){
            if(mappa.get(key).isVM() == adulti )
                result.add(mappa.get(key));
        }

        return new Libro(result);
    }

    public boolean esisteBarzellettaPrima(){
        return ultimiIndici.size()>1;
    }

    public Barzelletta getBarzellettaPrima(){
        int last = ultimiIndici.get(ultimiIndici.size()-2);
        ultimiIndici.removeLast();
        return mappa.get(keys.get(last));

    }

    private int getBarzelletteLette(){
        return barzelletteLette;
    }

    public void resetBarzelletteLette(Categoria categoria){
        ParseObject richiesta = ParseObject.create(SESSIONE_BARZELLETTE_OBJECT_KEY);
        richiesta.put(NUMERO_BARZELLETTE_KEY, barzelletteLette);
        if(categoria != null) {
            richiesta.put(CATEGORIA_KEY, categoria.toString());
        }
        else {
            richiesta.put(CATEGORIA_KEY, TUTTE_LE_CATEGORIE);
        }
        richiesta.saveInBackground();
        barzelletteLette = 0;
    }




}
