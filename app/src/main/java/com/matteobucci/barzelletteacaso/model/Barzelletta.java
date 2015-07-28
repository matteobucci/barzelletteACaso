package com.matteobucci.barzelletteacaso.model;

import java.io.Serializable;

/**
 * Created by Matti on 23/06/2015.
 */
public class Barzelletta implements Serializable, Comparable{

    private int ID;
    private String testo;
    private boolean adulti;
    private Categoria categoria;
    private boolean lunga;


    public Barzelletta(int ID, String testo, boolean adulti, Categoria categoria, boolean lunga){
        this.ID = ID;
        this.testo = testo;
        this.adulti = adulti;
        this.categoria = categoria;
        this.lunga = lunga;
    }

    //Costruttore da usare nel caso non si ha voglia di catalogare
    public Barzelletta(int ID, String testo){
        this(ID, testo, false, Categoria.UNDEFINED, false);//categoria.UNDEFINED);
    }

    public String toString(){
        return testo;
    }

    public int getID(){
        return ID;
    }

    public boolean isVM(){
        return adulti;
    }

    public Categoria getCategoria(){
        return categoria;
    }

    public boolean isLunga() {
        return lunga;
    }

    public boolean equals(Object another){
        if(another instanceof Barzelletta){
            return this.getID() == ((Barzelletta) another).getID();
        }
        else return false;
    }

    @Override
    public int compareTo(Object another) {
        if(another instanceof Barzelletta) {
            Barzelletta questa = (Barzelletta) another;
            return this.getID() - questa.getID();
        }
        else {
            return 0;
        }
    }
}
