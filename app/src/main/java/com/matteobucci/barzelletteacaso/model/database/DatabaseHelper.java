package com.matteobucci.barzelletteacaso.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matti on 02/07/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Informazioni di base
    public static final String DB_NAME = "barzellette.db";
    public static final int DB_VERSION = 1;
    public static final String  TABLE_NAME = "elenco_barzellette";

    //Informazioni sulla tabella elenco_barzellette
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_TESTO = "TESTO";
    public static final String COLUMN_CATEGORIA = "CATEGORIA";
    public static final String COLUMN_ADULTI = "ADULTI";

    //Stringa per la creazione
    public static final String CREATION_STRING = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TESTO + " STRING, " + COLUMN_CATEGORIA + " INTEGER, " + COLUMN_ADULTI + " INTEGER)";

    //Costruttore
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATION_STRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
