package com.matteobucci.barzelletteacaso.model;

import android.util.Log;

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

    private Map<Integer, Barzelletta> mappa; //Dovrebbe rendere più veloce il recuperare barzellette
    private List<Integer> keys;
    private LinkedList<Integer> ultimiIndici = new LinkedList<Integer>();
    private Random random;
    private int size;
    private static int BARZELLETTE_LASCIATE = 5;
    private int lastIndex;

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
        lastIndex = id;
        return mappa.get(id);
    }

    public Barzelletta getRandom(){
        int numero;

        do {
            numero = random.nextInt(size);
        }while(!checkLast(numero));

        Log.i("BARZELLETTA" , numero + "");
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




}
