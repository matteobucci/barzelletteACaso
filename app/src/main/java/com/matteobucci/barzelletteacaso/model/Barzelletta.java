package com.matteobucci.barzelletteacaso.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Matti on 23/06/2015.
 */
public class Barzelletta implements Serializable, Comparable, Parcelable {

    private int ID;

    private String testo;
    private boolean adulti;
    private Categoria categoria;
    private boolean lunga;
    private Tipo tipo;

    //Costruttore standard
    public Barzelletta(int ID, String testo, boolean adulti, Categoria categoria, boolean lunga, Tipo tipo){
        this.ID = ID;
        this.testo = testo;
        this.adulti = adulti;
        this.categoria = categoria;
        this.lunga = lunga;
        this.tipo = tipo;
    }

    //Costruttore da usare nel caso non si ha voglia di catalogare
    public Barzelletta(int ID, String testo){
        this(ID, testo, false, Categoria.UNDEFINED, false, Tipo.TESTO);//categoria.UNDEFINED);
    }

    public String toString(){ return testo; }

    public int getID(){
        return ID;
    }

    public boolean isVM(){
        return adulti;
    }

    public Categoria getCategoria(){
        return categoria;
    }

    public Tipo getTipo(){ return tipo; }

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


    public static final Parcelable.Creator<Barzelletta> CREATOR =
            new Parcelable.Creator<Barzelletta>() {

                @Override
                public Barzelletta createFromParcel(Parcel source) {
                    return new Barzelletta(source);
                }

                @Override
                public Barzelletta[] newArray(int size) {
                    return new Barzelletta[0];
                }
            };

    public Barzelletta(Parcel in){
        ID = in.readInt();
        testo = in.readString();

        if(in.readByte() == 1){
            adulti = true;
        }
        else{
            adulti = false;
        }
        categoria = Categoria.getCategoria(in.readInt());

        if(in.readByte() == 1){
            lunga = true;
        }
        else{
            lunga = false;
        }

        tipo = Tipo.getTipo(in.readInt());


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.getID());
        dest.writeString(this.toString());
        dest.writeByte((byte) (this.isVM() ? 1 : 0));
        dest.writeInt(this.getCategoria().getID());
        dest.writeByte((byte)(this.isLunga()? 1:0));
        dest.writeInt(this.getTipo().getID());
    }
}
