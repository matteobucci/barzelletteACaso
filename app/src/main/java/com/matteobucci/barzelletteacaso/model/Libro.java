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
    private int barzelletteIndietro=0;

    //Variabili per il funzionamento
    private Random random;
    private Map<Integer, Barzelletta> mappa;
    private List<Integer> keys;
    private LinkedList<Integer> ultimiIndici = new LinkedList<Integer>();
    private int size;
    private boolean nuovoMetodo = true; //Utilizza il nuovo metodo di scorrimento delle barzellette
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

    public void addBarzellette(Collection<Barzelletta> lista){
        if(mappa.containsKey(-1)){ //Se la mappa contiene l'avvertimento che non ci sono barzellette lo toglie
            mappa.clear();
        }

        for(Barzelletta attuale: lista){
            mappa.put(attuale.getID(), attuale); //Viene aggiunta ogni barzelletta alla mappa attuale
        }

        keys = new ArrayList<>(mappa.keySet());
        size = keys.size();
        Log.i(TAG, "Libro aggiornato con " + lista.size() + " barzellette in più, ora ne ha " + size);
    }


    public Barzelletta getByID(int id){
        return mappa.get(id);
    }

    public Barzelletta getRandom(){
        int numero;
        if(barzelletteIndietro==0) { //TODO:SISTEMARE STA COSA
            barzelletteLette++; //Utilizzato solo a scopi statistici

            do {
                numero = random.nextInt(size);
            }
            while (!checkLast(numero)); //Vengono generati nuovi numeri finchè non viene pescato uno che non esce da molto


        }
        else{
            barzelletteIndietro--;
            numero = ultimiIndici.get(ultimiIndici.size()-(1+barzelletteIndietro));
        }

        Log.i(TAG, numero + "");
        return mappa.get(keys.get(numero)); //Il numero random corrisponde ad un indice, al quale corrisponde una chiave (l'ID) che permette di recuperare la barzelletta

    }

    private boolean checkLast(int index){
        if(ultimiIndici.contains(index))
            return false;  //Il numero è presente tra gli ultimi
        else{
            ultimiIndici.addLast(index); //Viene aggiunto per ultimo il numero appena pescato se
            if(ultimiIndici.size()>size-BARZELLETTE_LASCIATE){
                ultimiIndici.removeFirst(); //Se la lista inizia a comprendere tutte le barzellette utilizzate (a meno di BARZELLETTE_LASCIATE vengono rimossi i primi indici usciti.
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
        return ultimiIndici.size()>barzelletteIndietro+1;
    }

    public Barzelletta getBarzellettaPrima(){
        int last;
        if(nuovoMetodo) {
            barzelletteIndietro++;
            last = ultimiIndici.get(ultimiIndici.size() - (1 + barzelletteIndietro)); //Si utilizza -2 perchè ultimiIndici vanno da 0 a size-1, e l'ultimo indice contiene la barzelletta attuale
        }
        else{
            last = ultimiIndici.get(ultimiIndici.size() - 2);
            ultimiIndici.removeLast();
        }


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
