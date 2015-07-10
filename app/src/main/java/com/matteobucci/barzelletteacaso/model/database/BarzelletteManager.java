package com.matteobucci.barzelletteacaso.model.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matti on 02/07/2015.
 */
public class BarzelletteManager {

    DatabaseGetter helper;
    Context context;
    SQLiteDatabase db;

    public BarzelletteManager(Context context){
        this.context = context;
        helper = new DatabaseGetter(context);
    }

    public void open(){
        try {
            helper.createDataBase();
            helper.openDataBase();
            db = helper.getReadableDatabase();

            if (db != null) {
                Log.i("DATABASE", "Avuto accesso al database");
            }
        }
        catch(SQLiteException e){
            Log.e("DATABASE", "Impssibile accedere al database");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if (db.isOpen()){
            db.close();
            Log.i("DATABASE", "Database chiuso");
        }
        else{
            Log.e("DATABASE", "Database già chiuso");
        }
    }

    public List<Barzelletta> getAllBarzellette(){
        this.open();


        Cursor cursor = db.query(DatabaseGetter.TABLE_NAME,
                new String[]{DatabaseGetter.COLUMN_ID, DatabaseGetter.COLUMN_TESTO, DatabaseGetter.COLUMN_CATEGORIA, DatabaseGetter.COLUMN_ADULTI},
                null,
                null,
                null,
                null,
                null);

        List<Barzelletta> result = new ArrayList<>();

        int id;
        String testo;
        Categoria categoria;
        boolean adulti;

        cursor.moveToFirst();

        while ( !cursor.isAfterLast()) {
            id = cursor.getInt(cursor.getColumnIndex(DatabaseGetter.COLUMN_ID));
            testo = cursor.getString(cursor.getColumnIndex(DatabaseGetter.COLUMN_TESTO));
            try{
                categoria = Categoria.getCategoria(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseGetter.COLUMN_CATEGORIA))));
            }
            catch(NumberFormatException e){
                categoria = Categoria.UNDEFINED;
            }

            if (!cursor.getString(cursor.getColumnIndex(DatabaseGetter.COLUMN_ADULTI)).equals("0"))
                adulti = true;
            else adulti = false;

            result.add(new Barzelletta(id, testo, adulti, categoria));
            cursor.moveToNext();
        }

         this.close();

        return result;
    }

    public List<Barzelletta> getBarzellettaByCategoria(Categoria categoria){
        List<Barzelletta> lista = this.getAllBarzellette();
        List<Barzelletta> result = new ArrayList<>();

        for(Barzelletta attuale : lista){
            if(attuale.getCategoria().equals(categoria))
                result.add(attuale);
        }

        return result;
    }

    public List<Barzelletta> getBarzellettaByAdulti(Boolean adulti){
        List<Barzelletta> lista = this.getAllBarzellette();
        List<Barzelletta> result = new ArrayList<>();

        for(Barzelletta attuale : lista){
            if(attuale.isVM() == adulti)
                result.add(attuale);
        }

        return result;
    }


}
