package com.matteobucci.barzelletteacaso.model;

/**
 * Created by matteo on 16/11/15.
 */
public enum Tipo {

    TESTO(0),
    IMMAGINE(1),
    UNDEFINED(-1);

    private int ID;

    Tipo(int ID){
        this.ID = ID;
    }

    public int getID(){
        return ID;
    }

    public static Tipo getTipo(int id) {
        if (id == 0) {
            return Tipo.TESTO;
        } else if (id == 1) {
            return Tipo.IMMAGINE;
        } else if (id == -1) {
            return Tipo.UNDEFINED;
        } else {
            return Tipo.TESTO;
        }
    }

}
