package com.matteobucci.barzelletteacaso;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.database.BarzelletteManager;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.listener.MainListener;
import com.matteobucci.barzelletteacaso.util.IabHelper;
import com.matteobucci.barzelletteacaso.util.IabResult;
import com.matteobucci.barzelletteacaso.util.Inventory;
import com.matteobucci.barzelletteacaso.util.Purchase;
import com.matteobucci.barzelletteacaso.view.NewFavoriteFragment;
import com.matteobucci.barzelletteacaso.model.listener.BarzellettaListener;
import com.matteobucci.barzelletteacaso.view.MainFragment;
import com.matteobucci.barzelletteacaso.view.SettingsActivity;
import com.matteobucci.barzelletteacaso.view.dialog.DialogPubblicita;
import com.matteobucci.barzelletteacaso.view.dialog.DialogScegliDonazione;
import com.parse.ParseAnalytics;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BarzellettaListener, IabHelper.OnIabPurchaseFinishedListener, MainListener {


    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout header;
    private NavigationView nvDrawer;

    private Categoria categoriaSelezionata;
    private Fragment fragment;
    private boolean firstAvvioFragment = true;


    private BarzelletteManager manager;
    private List<Barzelletta> tutteLeBarzellette;
    private boolean mIsPremium = false;
    private  Menu myMenu;

    IabHelper mHelper;

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                Log.e(StatStr.TAG_ACQUISTI, "IMPOSSIBILE VERIFICARE L'ACCOUNT");
            }
            else {
                boolean dona = inventory.hasPurchase("account_premium_base");
                boolean premium = inventory.hasPurchase("account_premium_donate");
                Log.i(StatStr.TAG_ACQUISTI, Boolean.toString(dona) + Boolean.toString(premium));
                mIsPremium = (dona || premium);

                PreferenceManager.getDefaultSharedPreferences(getApplication()).edit().putBoolean(StatStr.VERSIONE_PRO, mIsPremium).apply();
                Log.i(StatStr.TAG_ACQUISTI, "VERIFICA ACCOUNT EFFETTUATA, account premium: " + Boolean.toString(mIsPremium));

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_barzellette);

        if(!isNetworkAvailable(this)){
            Toast.makeText(this, "La rete non è disponibile, non verranno caricate le immagini", Toast.LENGTH_SHORT).show();
            this.getSharedPreferences("main", MODE_PRIVATE).edit().putBoolean(StatStr.NETWORK_AVIABLE_KEY, false).apply();
        }
        else{
            this.getSharedPreferences("main", MODE_PRIVATE).edit().putBoolean(StatStr.NETWORK_AVIABLE_KEY, true).apply();

        }

        manager = new BarzelletteManager(this);
            tutteLeBarzellette = manager.getAllBarzellette();


        FragmentManager fragmentManager = this.getSupportFragmentManager();
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





        String base64EncodedPublicKey = this.getResources().getString(R.string.codice_in_app_billing);
        // compute your public key and store it in base64EncodedPublicKey


        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(StatStr.TAG_ACQUISTI, "Problem setting up In-app Billing: " + result);
                }
                mHelper.queryInventoryAsync(mGotInventoryListener);
                // Hooray, IAB is fully set up!
            }
        });

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

            case R.id.action_dona:
                DialogFragment loadingDialog = new DialogScegliDonazione();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(loadingDialog, "loading");
                transaction.commitAllowingStateLoss();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(StatStr.VERSIONE_PRO, false)) {
            getMenuInflater().inflate(R.menu.menu_main_barzellette, menu);
        }
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
                    fragmentClass = NewFavoriteFragment.class;
                    break;
                case R.id.categoria_immagini:
                    categoriaSelezionata = Categoria.getCategoria(-1);
                    fragmentClass = MainFragment.class;
                    break;
                case R.id.categoria_consigliate:
                    categoriaSelezionata = Categoria.getCategoria(-2);
                    fragmentClass = MainFragment.class;
                    break;
                default:
                    fragmentClass = MainFragment.class;
            }

            try {
                if(categoriaSelezionata != null && categoriaSelezionata.equals(Categoria.IMMAGINI)){
                    fragment = MainFragment.newInstance(manager.getTutteImmagini(), categoriaSelezionata);
                }
                else if (categoriaSelezionata != null && categoriaSelezionata.equals(Categoria.CONSIGLIATE)){
                    fragment = MainFragment.newInstance(manager.getBarzelletteConsigliate(), categoriaSelezionata);
                }
                else if (fragmentClass.equals(MainFragment.class)) {
                    fragment = MainFragment.newInstance(tutteLeBarzellette, categoriaSelezionata);
                }
                else if(fragmentClass.equals(NewFavoriteFragment.class)) {
                    fragment = new NewFavoriteFragment();
                }
                else {
                    fragment = (Fragment) fragmentClass.newInstance();
                }

            } catch (Exception e) {
                e.printStackTrace();
                fragment = null;
            }

            //Inizia la transazione del fragment selezionato
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            // Seleziona la categoria selezionata, aggiorna il titolo e chiude il drawer
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
        }
        mDrawer.closeDrawers();
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

        //TODO:ATTTIVARE QUESTA FUNZIONE PER FARLO FUNZIONARE

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.isChangedString, false)){
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SettingsActivity.isChangedString, false).apply();
            tutteLeBarzellette = manager.getAllBarzellette();
            applyChange();
        }


    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SettingsActivity.isChangedString, false).apply();

        if (mHelper != null) mHelper.dispose();
        mHelper = null;

    }

    private void applyChange(){
        nvDrawer.getMenu().getItem(0).setChecked(true);
        setTitle(nvDrawer.getMenu().getItem(0).getTitle());
        categoriaSelezionata = null;
        fragment = MainFragment.newInstance(tutteLeBarzellette, categoriaSelezionata);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isFailure()) {
            Log.d(StatStr.TAG_ACQUISTI, "Error purchasing: " + result);
            return;
        }
        else if (info.getSku().equals("account_premium_base")) {
            Toast.makeText(this, "Grazie dell'acquisto, al prossimo avvio tutte le pubblicità saranno rimosse", Toast.LENGTH_LONG).show();
            mIsPremium = true;
            PreferenceManager.getDefaultSharedPreferences(getApplication()).edit().putBoolean(StatStr.VERSIONE_PRO, mIsPremium).apply();
            Log.i(StatStr.TAG_ACQUISTI, "COMPRATO IL BASE");
        }
        else if (info.getSku().equals("account_premium_donate")) {
            Toast.makeText(this, "Grazie dell'acquisto, al prossimo avvio tutte le pubblicità saranno rimosse", Toast.LENGTH_LONG).show();
            mIsPremium = true;
            PreferenceManager.getDefaultSharedPreferences(getApplication()).edit().putBoolean(StatStr.VERSIONE_PRO, mIsPremium).apply();
            Log.i(StatStr.TAG_ACQUISTI, "COMPRATO IL PREMIUM");
        }
    }

    @Override
    public void onAzione(int ID_AZIONE) {

        if(ID_AZIONE == StatStr.ID_ACQUISTO_BASE){
            mHelper.launchPurchaseFlow(this, StatStr.ACQUISTO_BASE, 10001,
                    this, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
        }
        else if (ID_AZIONE == StatStr.ID_ACQUISTO_PREMIUM){
            mHelper.launchPurchaseFlow(this, StatStr.ACQUISTO_PREMIUM, 10002,
                    this, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
        }
        else if (ID_AZIONE == StatStr.ID_MOSTRA_AD){
            //TODO:ATTIVARE ADS
        }
        else (Toast.makeText(this, "ID Acquisto non corrispondente", Toast.LENGTH_SHORT)).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(StatStr.TAG_ACQUISTI, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.i(StatStr.TAG_ACQUISTI, "onActivityResult handled by IABUtil.");
        }
    }
}
