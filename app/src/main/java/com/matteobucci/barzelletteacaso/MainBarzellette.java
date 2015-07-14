package com.matteobucci.barzelletteacaso;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.listener.BarzellettaListener;
import com.matteobucci.barzelletteacaso.view.FavoriteFragment;
import com.matteobucci.barzelletteacaso.view.FragmentMain;
import com.matteobucci.barzelletteacaso.view.SettingsActivity;

public class MainBarzellette extends AppCompatActivity implements BarzellettaListener {

    //Variabili della UI
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout header;
    private Categoria categoriaSelezionata = null;
    Fragment fragment = null;
    Barzelletta barzellettaToShare;
    boolean shareButtonEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_barzellette);

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
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        //Inizializza il primo fragment all'avvio dell'applicazione
        FragmentManager fragmentManager = this.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, FragmentMain.newInstance(this, categoriaSelezionata)).commit();

        header = (LinearLayout) findViewById(R.id.header);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_barzellette, menu);
        menu.findItem(R.id.action_share).setVisible(shareButtonEnabled);
        //shareItem = menu.findItem(R.id.action_share);
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

            fragment = null;
            Class fragmentClass;
            switch (menuItem.getItemId()) {
                case R.id.tutte_barzellette:
                    categoriaSelezionata = null;
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_1:
                    categoriaSelezionata = Categoria.getCategoria(1);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_2:
                    categoriaSelezionata = Categoria.getCategoria(2);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_3:
                    categoriaSelezionata = Categoria.getCategoria(3);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_4:
                    categoriaSelezionata = Categoria.getCategoria(4);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_5:
                    categoriaSelezionata = Categoria.getCategoria(5);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_6:
                    categoriaSelezionata = Categoria.getCategoria(6);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_7:
                    categoriaSelezionata = Categoria.getCategoria(7);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_8:
                    categoriaSelezionata = Categoria.getCategoria(8);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.categoria_9:
                    categoriaSelezionata = Categoria.getCategoria(9);
                    fragmentClass = FragmentMain.class;
                    break;
                case R.id.lista_preferiti:
                    categoriaSelezionata = null;
                    fragmentClass = FavoriteFragment.class;
                    break;
                default:
                    fragmentClass = FragmentMain.class;
            }

            try {
                if (fragmentClass.equals(FragmentMain.class)) {
                    fragment = FragmentMain.newInstance(this, categoriaSelezionata);
                    shareButtonEnabled = true;
                    invalidateOptionsMenu();
                }
                else if(fragmentClass.equals(FavoriteFragment.class)) {
                    fragment = FavoriteFragment.newInstance(this);
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
            i.putExtra(android.content.Intent.EXTRA_TEXT, barzellettaToShare.toString());
            startActivity(Intent.createChooser(i, "Condividi con"));
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onChangeBarzelletta(int color, int darkerColor, Barzelletta barzelletta) {
        toolbar.setBackgroundColor(color);
        header.setBackgroundColor(darkerColor);
        barzellettaToShare = barzelletta;
    }
}
