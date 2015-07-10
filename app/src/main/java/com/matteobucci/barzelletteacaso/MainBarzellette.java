package com.matteobucci.barzelletteacaso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.Libro;
import com.matteobucci.barzelletteacaso.model.database.BarzelletteManager;

public class MainBarzellette extends Activity {

    //Variabili del model
    private BarzelletteManager manager;
    private Libro lista;


    //Variabili della UI
    private TextView textView;
    private Button nextButton;
    private ImageButton favoriteButton;
    private Button cliccami;

    //Variabili che servono a gestire il programma
    Barzelletta barzellettaAttuale = null;
    Favorite favoriti;

    //La barzelleta attuale è favorita?
    private boolean isActualFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_barzellette);
        manager = new BarzelletteManager(this);
        lista = new Libro(manager.getAllBarzellette());
        setUIVar();
        setListeners();
        favoriti = Favorite.getInstance(this);
        

    }


    private void setUIVar(){
        textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        nextButton = (Button) findViewById(R.id.nextButton);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        cliccami = (Button) findViewById(R.id.cliccami);
    }

    private void setListeners(){

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBarzelletta();
                textView.setText(barzellettaAttuale.toString() + " " + barzellettaAttuale.getID() );
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barzellettaAttuale != null) {
                    if (favoriti.contains(barzellettaAttuale)) {
                        favoriti.remove(barzellettaAttuale);
                        Toast.makeText(MainBarzellette.this, "Preferito rimosso", Toast.LENGTH_SHORT).show();
                    } else {
                        favoriti.add(barzellettaAttuale);
                        Toast.makeText(MainBarzellette.this, "Preferito aggiunto", Toast.LENGTH_SHORT).show();
                    }
                    checkBarzelletta();
                }
            }
        });

        cliccami.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainBarzellette.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onPause(){
        super.onPause();
        if(favoriti !=null){
            favoriti.saveFavorite();
        }
    }

    public void onResume(){
        super.onResume();
        if(favoriti != null){
            favoriti.loadFavorite();
        }
    }

    private void setBarzelletta(){
        barzellettaAttuale = lista.getRandom();
        isActualFavorite = favoriti.contains(barzellettaAttuale);
        checkBarzelletta();
    }

    private void checkBarzelletta(){
        if(favoriti.contains(barzellettaAttuale)){
            favoriteButton.setColorFilter(android.graphics.Color.parseColor("#741f14"));
        }
        else{
            favoriteButton.setColorFilter(null);
        }
    }
