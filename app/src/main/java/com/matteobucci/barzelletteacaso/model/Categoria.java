package com.matteobucci.barzelletteacaso.model;

/**
 * Created by Matti on 23/06/2015.
 */
public enum Categoria {
    UNDEFINED(0),
    ANIMALI(1),
    CARABINIERI(2),
    DOTTORI(3),
    TECNOLOGIA(4),
    INDOVINELLI(5),
    FREDDURE(6),
    UOMINI(7),
    DONNE(8),
    VARIE(9);

    private int ID;

    Categoria(int ID){
        this.ID = ID;
    }


    public static Categoria getCategoria(int id){
        if(id == 0){
            return Categoria.UNDEFINED;
        }
        else if(id == 1){
            return Categoria.ANIMALI;
        }
        else if(id == 2){
            return Categoria.CARABINIERI;
        }
        else if(id == 3){
            return Categoria.DOTTORI;
        }
        else if(id == 4){
            return Categoria.TECNOLOGIA;
        }
        else if(id == 5){
            return Categoria.INDOVINELLI;
        }
        else if(id == 6){
            return Categoria.FREDDURE;
        }
        else if(id == 7){
            return Categoria.UOMINI;
        }
        else if(id == 8){
            return Categoria.DONNE;
        }
        else if(id == 9){
            return Categoria.VARIE;
        }

        else {
            return Categoria.UNDEFINED;
        }

    }


}
