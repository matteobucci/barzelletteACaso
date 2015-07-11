package com.matteobucci.barzelletteacaso;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.Libro;
import com.matteobucci.barzelletteacaso.model.database.BarzelletteManager;

public class MainBarzellette extends AppCompatActivity implements FragmentMain.OnFragmentInteractionListener {

    //Variabili del model
    private BarzelletteManager manager;
    private Libro lista;


    //Variabili della UI
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
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

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Setup drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);


        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, FragmentMain.newInstance()).commit();



        manager = new BarzelletteManager(this);
        lista = new Libro(manager.getBarzellettaByCategoria(Categoria.FREDDURE));
       // setUIVar();
       // setListeners();
        favoriti = Favorite.getInstance(this);


    }


    private void setUIVar() {
        textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        nextButton = (Button) findViewById(R.id.nextButton);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        cliccami = (Button) findViewById(R.id.cliccami);
    }

    private void setListeners() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBarzelletta();
                textView.setText(barzellettaAttuale.toString() + " " + barzellettaAttuale.getID());
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

    public void onPause() {
        super.onPause();
        if (favoriti != null) {
            favoriti.saveFavorite();
        }
    }

    public void onResume() {
        super.onResume();
        if (favoriti != null) {
            favoriti.loadFavorite();
        }
    }

    private void setBarzelletta() {
        barzellettaAttuale = lista.getRandom();
        isActualFavorite = favoriti.contains(barzellettaAttuale);
        checkBarzelletta();
    }

    private void checkBarzelletta() {
        if (favoriti.contains(barzellettaAttuale)) {
            favoriteButton.setColorFilter(android.graphics.Color.parseColor("#741f14"));
        } else {
            favoriteButton.setColorFilter(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment;

        Class fragmentClass ;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = FragmentMain.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = FragmentMain.class;
                break;
            case R.id.nav_third_fragment:
               fragmentClass = FragmentMain.class;
                break;
            default:
                fragmentClass = FragmentMain.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            fragment = null;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
