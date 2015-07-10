package com.matteobucci.barzelletteacaso.model;

/**
 * Created by Matti on 23/06/2015.
 */
public enum Categoria {
    UNDEFINED(0),
    FREDDURE(1),
    SPORCHE(2),
    ALTRO(3);

    private int ID;

    Categoria(int ID){
        this.ID = ID;
    }


    public static Categoria getCategoria(int id){
        if(id == 0){
            return Categoria.UNDEFINED;
        }
        else if(id == 1){
            return Categoria.FREDDURE;
        }
        else if(id == 2){
            return Categoria.SPORCHE;
        }
        else if(id == 3){
            return Categoria.ALTRO;
        }
        else {
            return Categoria.UNDEFINED;
        }

    }


}
