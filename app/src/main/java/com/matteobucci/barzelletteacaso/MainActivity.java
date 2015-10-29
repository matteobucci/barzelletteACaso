package com.matteobucci.barzelletteacaso;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.matteobucci.barzelletteacaso.database.BarzelletteManager;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.listener.BarzellettaListener;
import com.matteobucci.barzelletteacaso.view.FavoriteFragment;
import com.matteobucci.barzelletteacaso.view.MainFragment;
import com.matteobucci.barzelletteacaso.view.SettingsActivity;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BarzellettaListener {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout header;
    private NavigationView nvDrawer;

    private Categoria categoriaSelezionata;
    private Fragment fragment;
    private boolean firstAvvioFragment = true;

    private Barzelletta barzellettaToShare;
    private boolean shareButtonEnabled = true;

    private BarzelletteManager manager;
    private List<Barzelletta> tutteLeBarzellette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_barzellette);

        manager = new BarzelletteManager(this);
            tutteLeBarzellette = manager.getAllBarzellette();


        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, MainFragment.newInstance(tutteLeBarzellette, categoriaSelezionata)).commit();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);

        // Set the menu icon instead of the launcher icon.

        // Setup drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        //Inizializza il primo fragment all'avvio dell'applicazione
        header = (LinearLayout) nvDrawer.findViewById(R.id.header);

        //Segnala a parse l'avvenuta apertura dell'applicazione
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_share:
                share();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_barzellette, menu);
        menu.findItem(R.id.action_share).setVisible(shareButtonEnabled);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.getMenu().getItem(0).setChecked(true);
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
        if(menuItem.getItemId() == R.id.item_impostazioni){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        else if(!menuItem.isChecked()) {
            firstAvvioFragment=true;
            fragment = null;
            Class fragmentClass;
            switch (menuItem.getItemId()) {
                case R.id.tutte_barzellette:
                    categoriaSelezionata = null;
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_1:
                    categoriaSelezionata = Categoria.getCategoria(1);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_2:
                    categoriaSelezionata = Categoria.getCategoria(2);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_3:
                    categoriaSelezionata = Categoria.getCategoria(3);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_4:
                    categoriaSelezionata = Categoria.getCategoria(4);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_5:
                    categoriaSelezionata = Categoria.getCategoria(5);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_6:
                    categoriaSelezionata = Categoria.getCategoria(6);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_7:
                    categoriaSelezionata = Categoria.getCategoria(7);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_8:
                    categoriaSelezionata = Categoria.getCategoria(8);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_9:
                    categoriaSelezionata = Categoria.getCategoria(9);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_10:
                    categoriaSelezionata = Categoria.getCategoria(10);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.lista_preferiti:
                    categoriaSelezionata = null;
                    fragmentClass = FavoriteFragment.class;
                    break;
                default:
                    fragmentClass = MainFragment.class;
            }

            try {
                if (fragmentClass.equals(MainFragment.class)) {
                    fragment = MainFragment.newInstance(tutteLeBarzellette, categoriaSelezionata);
                    shareButtonEnabled = true;
                    invalidateOptionsMenu();
                }
                else if(fragmentClass.equals(FavoriteFragment.class)) {
                    fragment = FavoriteFragment.newInstance();
                    shareButtonEnabled = false;
                    invalidateOptionsMenu();
                }
                else {
                    fragment = (Fragment) fragmentClass.newInstance();
                    shareButtonEnabled = true;
                    invalidateOptionsMenu();
                }

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
        }
        mDrawer.closeDrawers();
    }

    private void share() {
            Intent i = new Intent(android.content.Intent.ACTION_SEND);
            i.setType("text/plain");

        Map<String, String> dimensions = new HashMap<String, String>();
        dimensions.put("id_barzelletta", Integer.toString(barzellettaToShare.getID()));
        ParseAnalytics.trackEventInBackground("barzelletta_condivisa", dimensions);

        
            i.putExtra(android.content.Intent.EXTRA_TEXT, barzellettaToShare.toString() + "\n\n Presa da Barzellette a caso, scarica l'applicazione! " + this.getResources().getString(R.string.url_app_playstore));
            startActivity(Intent.createChooser(i, getString(R.string.condividi_con)));
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onChangeBarzelletta(int color, int darkerColor, Barzelletta barzelletta) {

        if(header!=null) {
            header.setBackgroundColor(darkerColor);
        }

        barzellettaToShare = barzelletta;

        if(firstAvvioFragment){
            firstAvvioFragment=false;
            toolbar.setBackgroundColor(color);
        }
        else {

            Integer colorFrom;
            Drawable background = toolbar.getBackground();
            if (background instanceof ColorDrawable)
                colorFrom = ((ColorDrawable) background).getColor();
            else colorFrom = 0;

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    toolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
                }

            });

            colorAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            colorAnimation.start();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.isChangedString, false)){
        //    Toast.makeText(this, "Preferenze cambiate, si consiglia di riavviare l'applicazione", Toast.LENGTH_LONG).show();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SettingsActivity.isChangedString, false).apply();
            tutteLeBarzellette = manager.getAllBarzellette();
            applyChange();

        }


        FragmentManager fragmentManager = this.getFragmentManager();


        if(nvDrawer.getMenu().getItem(1).isChecked()){
            fragmentManager.beginTransaction().replace(R.id.flContent, FavoriteFragment.newInstance()).commit();
        }


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SettingsActivity.isChangedString, false).apply();
    }

    private void applyChange(){
        nvDrawer.getMenu().getItem(0).setChecked(true);
        setTitle(nvDrawer.getMenu().getItem(0).getTitle());
        categoriaSelezionata = null;
        fragment = MainFragment.newInstance(tutteLeBarzellette, categoriaSelezionata);
        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

}
