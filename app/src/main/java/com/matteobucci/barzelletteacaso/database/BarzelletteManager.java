package com.matteobucci.barzelletteacaso.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.matteobucci.barzelletteacaso.MainActivity;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.Tipo;
import com.matteobucci.barzelletteacaso.view.SettingsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Matti on 02/07/2015.
 */

public class BarzelletteManager {

    private final String TAG = this.getClass().getSimpleName();

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
                Log.i(TAG, "Avuto accesso al database");
            }
        }
        catch(SQLiteException e){
            Log.e(TAG, "Impossibile accedere al database");
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            Log.e(TAG, "Errore tipico del database" + e.getMessage());
        }
    }

    public void close(){
        if (db.isOpen()){
            if(db.inTransaction()) {
                Log.e(TAG, "ANCORA IN TRANSICTION?");
                db.endTransaction();
            }
            db.close();
            Log.i(TAG, "Database chiuso");
        }
        else{
            Log.e(TAG, "Database già chiuso");
        }
    }

    public List<Barzelletta> getAllBarzellette(){
        this.open();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean immaginiPref = sharedPref.getBoolean(SettingsActivity.immagineString, true);
        boolean networkPref = sharedPref.getBoolean(MainActivity.NETWORK_AVIABLE_KEY, true);

        List<Barzelletta> result = new ArrayList<>();

        if(db!=null) {

            Cursor cursor = getCursor(immaginiPref, networkPref, false, false);

            result = costruisciLista(cursor, sharedPref);

            this.close();
        }


        return result;
    }

    public List<Barzelletta> getBarzelletteConsigliate(){
        this.open();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        List<Barzelletta> result = new ArrayList<>();

        if(db!=null) {

            Cursor cursor = getCursor(false, false, true, false);

            result = costruisciLista(cursor, sharedPref);

            this.close();
        }


        return result;
    }

    public List<Barzelletta> getTutteImmagini() {
        this.open();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        List<Barzelletta> result = new ArrayList<>();

        if(db!=null) {

            Cursor cursor = getCursor(false, false, false, true);

            result = costruisciLista(cursor, sharedPref);

            this.close();
        }


        return result;
    }

    private Cursor getCursor(Boolean immaginiPref, Boolean networkPref, Boolean consigliate, Boolean soloImmagini) {

        Cursor cursor;
        if(soloImmagini){
            cursor = db.query(DatabaseGetter.TABLE_NAME,
                    new String[]{DatabaseGetter.COLUMN_ID, DatabaseGetter.COLUMN_TESTO, DatabaseGetter.COLUMN_CATEGORIA, DatabaseGetter.COLUMN_ADULTI, DatabaseGetter.COLUMN_LUNGA, DatabaseGetter.COLUMN_TIPO, DatabaseGetter.COLUMN_CONSIGLIATA},
                    DatabaseGetter.COLUMN_TIPO + " =?",
                    new String[]{"immagine"},
                    null,
                    null,
                    null);
        }
        else if(consigliate){
            cursor = db.query(DatabaseGetter.TABLE_NAME,
                    new String[]{DatabaseGetter.COLUMN_ID, DatabaseGetter.COLUMN_TESTO, DatabaseGetter.COLUMN_CATEGORIA, DatabaseGetter.COLUMN_ADULTI, DatabaseGetter.COLUMN_LUNGA, DatabaseGetter.COLUMN_TIPO, DatabaseGetter.COLUMN_CONSIGLIATA},
                    DatabaseGetter.COLUMN_CONSIGLIATA +" =?",
                    new String[]{"1"},
                    null,
                    null,
                    null);
        }
        else if(immaginiPref && networkPref) {

            cursor = db.query(DatabaseGetter.TABLE_NAME,
                    new String[]{DatabaseGetter.COLUMN_ID, DatabaseGetter.COLUMN_TESTO, DatabaseGetter.COLUMN_CATEGORIA, DatabaseGetter.COLUMN_ADULTI, DatabaseGetter.COLUMN_LUNGA, DatabaseGetter.COLUMN_TIPO, DatabaseGetter.COLUMN_CONSIGLIATA},
                    DatabaseGetter.COLUMN_CONSIGLIATA +" <>?",
                    new String[]{"1"},
                    null,
                    null,
                    null);
        }
        else{
            cursor = db.query(DatabaseGetter.TABLE_NAME,
                    new String[]{DatabaseGetter.COLUMN_ID, DatabaseGetter.COLUMN_TESTO, DatabaseGetter.COLUMN_CATEGORIA, DatabaseGetter.COLUMN_ADULTI, DatabaseGetter.COLUMN_LUNGA, DatabaseGetter.COLUMN_TIPO, DatabaseGetter.COLUMN_CONSIGLIATA},
                    DatabaseGetter.COLUMN_TIPO + " =?" + "and " + DatabaseGetter.COLUMN_CONSIGLIATA +" <>?",
                    new String[]{"testo", "1"},
                    null,
                    null,
                    null);

        }




        return cursor;
    }


    private List<Barzelletta> costruisciLista(Cursor cursor, SharedPreferences sharedPref){

        List<Barzelletta> result = new ArrayList<>();
        boolean adultiPref = sharedPref.getBoolean(SettingsActivity.adultiString, false);
        boolean lungaPref = sharedPref.getBoolean(SettingsActivity.lungheString, false);

        int id;
        String testo;
        Categoria categoria;
        boolean adulti;
        boolean lunga;
        Tipo tipo;
        boolean consigliata;

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            id = cursor.getInt(cursor.getColumnIndex(DatabaseGetter.COLUMN_ID));
            testo = cursor.getString(cursor.getColumnIndex(DatabaseGetter.COLUMN_TESTO));
            try {
                categoria = Categoria.getCategoria(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseGetter.COLUMN_CATEGORIA))));
            } catch (NumberFormatException e) {
                categoria = Categoria.UNDEFINED;
            }

            adulti = (!cursor.getString(cursor.getColumnIndex(DatabaseGetter.COLUMN_ADULTI)).equals("0"));
            lunga = (cursor.getInt(cursor.getColumnIndex(DatabaseGetter.COLUMN_LUNGA)) != 0);

            String stringTipo = cursor.getString(cursor.getColumnIndex(DatabaseGetter.COLUMN_TIPO));
            if(stringTipo.equals("testo")){
                tipo = Tipo.TESTO;
            }
            else if(stringTipo.equals("immagine")){
                tipo = Tipo.IMMAGINE;
            }
            else{
                tipo = Tipo.TESTO;
            }

            boolean isToAdd = (   !(lungaPref&&lunga)&&!(adultiPref&&adulti) );
            if(isToAdd){
                result.add(new Barzelletta(id, testo, adulti, categoria, lunga, tipo));

            }

            cursor.moveToNext();

        }

        return result;
    }



}
