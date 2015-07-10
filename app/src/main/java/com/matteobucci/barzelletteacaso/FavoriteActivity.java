package com.matteobucci.barzelletteacaso;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;

public class FavoriteActivity extends Activity {

    Favorite favorite = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        favorite = Favorite.getInstance(this);
        TextView helloWorld = (TextView) findViewById(R.id.helloWorld);
        StringBuilder sb = new StringBuilder();
        for(Barzelletta attuale : favorite.getFavoriteList()){
            sb.append(attuale.toString() + " \n");
        }
        helloWorld.setText(sb.toString());
    }






}
