package com.matteobucci.barzelletteacaso.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.database.BarzelletteManager;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.Libro;

import java.util.List;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        BarzelletteManager  manager = new BarzelletteManager(this);
        List<Barzelletta> lista = manager.getAllBarzellette();
        Categoria[] categorie = Categoria.values();
        int[] numOfBarze = new int[categorie.length];

        for(int i=0; i<numOfBarze.length; i++){
            numOfBarze[i] = 0;
        }

        for(Barzelletta attuale: lista){
           numOfBarze[attuale.getCategoria().getID()]++;
        }

        StringBuilder sb = new StringBuilder();

        int i = 0;
        for(Categoria attuale: Categoria.values()){
            if(i!=0)
            sb.append(attuale.toString() + " : " + numOfBarze[i] + "\n");
            i++;
        }

       TextView categorieTextView = (TextView) findViewById(R.id.textViewInfoCategorie);

        categorieTextView.setText(sb.toString());
    }


}
