package com.matteobucci.barzelletteacaso.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.matteobucci.barzelletteacaso.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Matti on 07/07/2015.
 */
public class DatabaseGetter extends SQLiteOpenHelper {

    private final String TAG = this.getClass().getSimpleName();

    private static final int DB_VERSION = 38;

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.matteobucci.barzelletteacaso/databases/";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    //Informazioni di base
    public static final String DB_NAME = "barzellette.db";
    public static final String  TABLE_NAME = "elenco_barzellette";

    //Informazioni sulla tabella elenco_barzellette
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_TESTO = "TESTO";
    public static final String COLUMN_CATEGORIA = "CATEGORIA";
    public static final String COLUMN_ADULTI = "ADULTI";
    public static final String COLUMN_LUNGA = "LUNGA";
    public static final String COLUMN_TIPO = "TIPO";
    public static final String COLUMN_CONSIGLIATA = "CONSIGLIATA";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseGetter(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;

    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.

            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copying database");

            }finally {
                this.close();
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;
        boolean isToUpgrade = true;



        SharedPreferences sharedPref = myContext.getSharedPreferences("db_version", Context.MODE_PRIVATE);
        int versionRead = sharedPref.getInt("version", 0);
        isToUpgrade = (versionRead==DB_VERSION);


        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

       return (isToUpgrade) && (checkDB != null ? true : false);
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

        SharedPreferences sharedPref = myContext.getSharedPreferences("db_version", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("version", DB_VERSION);
        editor.apply();

        Log.i(TAG, "DATABASE AGGIORNATO");


    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
       // myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    @Override
    public  void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}
