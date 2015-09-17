package com.matteobucci.barzelletteacaso;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matteobucci.barzelletteacaso.database.BarzelletteManager;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.Favorite;
import com.matteobucci.barzelletteacaso.view.SettingsActivity;
import com.matteobucci.barzelletteacaso.view.support.ColorList;

public class BarzellettaDelGiorno extends AppCompatActivity {

    private Barzelletta barzellettaDelGiorno;
    private TextView barzellettaTextView;
    private ColorList colorList;
    private Favorite favorite;
    private ImageButton favoriteButton;
    private  Boolean isActualFavorite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barzelletta_del_giorno);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle("Barzelletta del giorno");

        Intent intent = this.getIntent();

        colorList = new ColorList();
        int color = colorList.getColor();
        toolbar.setBackgroundColor(color);
        (findViewById(R.id.layoutBarzellette)).setBackgroundColor(color);

        if(intent==null) {
            barzellettaDelGiorno = new Barzelletta(-1, "Nessuna barzelletta del giorno", false, Categoria.UNDEFINED, false);
        }
        else{
            barzellettaDelGiorno = intent.getParcelableExtra("barzelletta");
        }

        favorite = Favorite.getInstance(this);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        checkBarzelletta();
        barzellettaTextView = (TextView) findViewById(R.id.barzellettaDelGiornoTextView);
        barzellettaTextView.setText(barzellettaDelGiorno.toString());
        barzellettaTextView.setMovementMethod(new ScrollingMovementMethod());


        int textSize =  PreferenceManager.getDefaultSharedPreferences(this).getInt("SEEKBAR_VALUE", 10)+10;
        boolean sfondoChiaro =  PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.sfondoChiaro, false);
        barzellettaTextView.setTextSize(textSize);
        if(sfondoChiaro){
            barzellettaTextView.setBackgroundColor(Color.parseColor("#32ffffff"));
            barzellettaTextView.setTextColor(Color.parseColor("#FF000000"));
        }
        else {
            barzellettaTextView.setBackgroundColor(Color.TRANSPARENT);
            //  textView.setTextColor(Color.parseColor("#ff757575"));
            barzellettaTextView.setTextColor(getResources().getColor(R.color.abc_secondary_text_material_light));
        }


        setListeners();
    }

    private void setListeners() {
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isActualFavorite){
                    setFavorite();
                }
                else{
                    cancelFavorite();
                }
                checkBarzelletta();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_barzellette, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_share) {
            share();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void share() {
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_TEXT, barzellettaDelGiorno.toString() + "\n\n Presa da Barzellette a caso, scarica l'applicazione! " + this.getResources().getString(R.string.url_app_playstore));
        startActivity(Intent.createChooser(i, getString(R.string.condividi_con)));
    }

    private void setFavorite(){
        favorite.add(barzellettaDelGiorno);
    }

    private void cancelFavorite(){
        favorite.remove(barzellettaDelGiorno);
    }


    private void checkBarzelletta() {
        isActualFavorite = !favorite.contains(barzellettaDelGiorno);

        if (isActualFavorite) {
            //    favoriteButton.setColorFilter(android.graphics.Color.parseColor("#741f14"));
            favoriteButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.favorite_heart_disabled));
        } else {
            //       favoriteButton.setColorFilter(null);
            favoriteButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.favorite_heart_enabled));

        }
    }


}



