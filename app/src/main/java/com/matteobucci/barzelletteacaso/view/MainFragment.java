package com.matteobucci.barzelletteacaso.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.matteo.rippleanimation.RippleBackground;
import com.matteobucci.barzelletteacaso.model.Favorite;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.Libro;
import com.matteobucci.barzelletteacaso.model.Tipo;
import com.matteobucci.barzelletteacaso.model.TouchImageView;
import com.matteobucci.barzelletteacaso.model.listener.BarzellettaListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.matteobucci.barzelletteacaso.view.dialog.AppRater;
import com.matteobucci.barzelletteacaso.view.dialog.DialogProponi;
import com.matteobucci.barzelletteacaso.view.dialog.DialogScegliImmagini;
import com.matteobucci.barzelletteacaso.view.dialog.DialogSegnala;
import com.matteobucci.barzelletteacaso.view.dialog.DialogSuggerimento;
import com.matteobucci.barzelletteacaso.view.support.ColorList;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment implements GestureDetector.OnGestureListener {

    private static final String BARZELLETTA_CONDIVISA_OBJECT_KEY = "Condivisa_Nuova";
    private static final String ID_KEY = "id_barzelletta";
    private static final String TESTO_KEY = "testo_barzelletta";
    private static final String CATEGORIA_KEY = "categoria";
    private static final String BARZELLETTA_PREFERITA_OBJECT_KEY = "Preferito";
    private static final String VERSIONE_KEY = "versione";
    private final String TAG = this.getClass().getSimpleName();

    private static int MODE_NEXT = 1;
    private static int MODE_PREVIOUS = -1;

    //Variabili del model
    private Libro lista;
    private Favorite favoriti;
    private Categoria categoria;
    private Barzelletta barzellettaAttuale = null;
    private Bitmap immagineAttuale = null;
    private boolean isActualFavorite;
    private boolean inCaricamento = false;

    //Variabili della UI
    private TextView  textView;
    private Button nextButton;
    private Button precedenteButton;
    private ImageButton favoriteButton;
    private RelativeLayout layoutBarzellette;
    private LinearLayout layoutInferiore;
    private RippleBackground rippleBackground;
    private FrameLayout layoutImmagini;
    private TouchImageView immagine;
    private ProgressBar progressBar;

    //Variabili della pubblicità
    private AdView mAdView;
    private AdRequest adRequest;

    //Variabili delle gesture
    private boolean swipeEnabled;
    private GestureDetector myGestDetector;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    //Variabili delle preferenze
    boolean sfondoChiaro = false;
    private int textSize;

    //Altre variabili
    private ColorList colors;
    private Context context;
    private BarzellettaListener colorListener;
    private boolean versionePro;
    private boolean appenaAvviata=true;
    private boolean fallimentoCaricamento = false;


    public static MainFragment newInstance(List<Barzelletta> lista, Categoria categoria) {

        MainFragment fragment = new MainFragment();
        fragment.categoria = categoria;
        fragment.lista = MainFragment.filtra(lista, categoria);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        versionePro = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("versione_pro", false);

        favoriti = Favorite.getInstance(context);
        favoriti.loadFavorite();
        AppRater.show(context, getFragmentManager());
        scegliImmagini();

    }

    private void scegliImmagini() {
        if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("primoAvvio", false)){
            DialogScegliImmagini dialogImmagini = new DialogScegliImmagini();
            dialogImmagini.show(getFragmentManager(), "");
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("primoAvvio", true).apply();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_segnala){
            segnala();
            return true;
        }
        else if(id == R.id.action_proponi){
            proproni();
            return true;
        }
        else if(id == R.id.action_share){
            share();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_main, container, false);
        setUIVar(myInflatedView);
        setListeners();
        setBarzelletta(MODE_NEXT);
        return myInflatedView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        try {
            colorListener = (BarzellettaListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(favoriti != null && lista!=null){
            favoriti.saveFavorite();
            lista.resetBarzelletteLette(categoria);
        }
     //   manager.close();
    }

    @Override
    public void onStart(){
        super.onStart();
        swipeEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.swipeString, true);
        textSize =  PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 10)+10;
        sfondoChiaro =  PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getBoolean(SettingsActivity.sfondoChiaro, false);
        textView.setTextSize(textSize);
        if(sfondoChiaro){
            textView.setBackgroundColor(Color.parseColor("#32ffffff"));
            textView.setTextColor(Color.parseColor("#FF000000"));
        }
        else {
            textView.setBackgroundColor(Color.TRANSPARENT);
          //  textView.setTextColor(Color.parseColor("#ff757575"));
            textView.setTextColor(getResources().getColor(R.color.abc_secondary_text_material_light));
        }
        abilitaPro();

    }



    private void setUIVar(View w) {
        textView = (TextView) w.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        nextButton = (Button) w.findViewById(R.id.nextButton);
        precedenteButton = (Button) w.findViewById(R.id.buttonPrecedente);
        favoriteButton = (ImageButton) w.findViewById(R.id.favoriteButton);
        layoutBarzellette = (RelativeLayout) w.findViewById(R.id.layoutBarzellette);
        layoutInferiore = (LinearLayout) w.findViewById(R.id.layoutInferiore);
        rippleBackground=(RippleBackground)w.findViewById(R.id.content);
        layoutImmagini = (FrameLayout)w.findViewById(R.id.layoutImmagini);
        immagine = (TouchImageView)w.findViewById(R.id.imageViewBarzzellette);
        progressBar = (ProgressBar)w.findViewById(R.id.progressBar);
        //ZONA PUBBLICITA
        mAdView = (AdView) w.findViewById(R.id.adView);
        //FINE ZONA
        colors = new ColorList();
        textView.setTextSize(textSize);
      //  setColor();
    }

    private void setListeners() {

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBarzelletta(MODE_NEXT);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barzellettaAttuale != null && !inCaricamento) {

                    ParseObject richiesta = ParseObject.create(BARZELLETTA_PREFERITA_OBJECT_KEY);
                    richiesta.put(ID_KEY, barzellettaAttuale.getID());
                    richiesta.put(CATEGORIA_KEY, barzellettaAttuale.getCategoria().toString());
                    richiesta.put(VERSIONE_KEY, getString(R.string.application_version));
                    richiesta.saveInBackground();


                    if (favoriti.contains(barzellettaAttuale)) {
                        favoriti.remove(barzellettaAttuale);
                        cancellaThumbnail();
                        Toast.makeText(context, "Preferito rimosso", Toast.LENGTH_SHORT).show();
                    } else {
                        rippleBackground.startRippleAnimation();
                        favoriti.add(barzellettaAttuale);
                        if(barzellettaAttuale.getTipo().equals(Tipo.IMMAGINE)) {
                            saveThumbnail(immagineAttuale);
                        }
                        Toast.makeText(context, "Preferito aggiunto", Toast.LENGTH_SHORT).show();
                    }
                    checkBarzelletta();
                }
            }
        });

        precedenteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBarzelletta(MODE_PREVIOUS);
            }
        });

        myGestDetector = new GestureDetector(context, this);

        View.OnTouchListener gesturelistener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (swipeEnabled && immagine.getCurrentZoom() == 1.0) {
                    myGestDetector.onTouchEvent(event);
                }
                return false;
            }
        };

        textView.setOnTouchListener(gesturelistener);
        immagine.setOnTouchListener(gesturelistener);




    }

    private void setBarzellettaPrecendete() {

        isActualFavorite = favoriti.contains(barzellettaAttuale);
        textView.setText(barzellettaAttuale.toString());
        setColor();
        textView.post(new Runnable() {

            @Override
            public void run() {
                textView.scrollTo(0, 0);
            }
        });
        checkBarzelletta();
    }

    private void setColor() {

        final int color = colors.getColor();

        if(appenaAvviata){
            appenaAvviata=false;
            layoutBarzellette.setBackgroundColor(color);
            nextButton.setTextColor(color);
            precedenteButton.setTextColor(color);
            colorListener.onChangeBarzelletta(color, colors.getAssociateColor(), barzellettaAttuale);
        }
        else {

            colorListener.onChangeBarzelletta(color, colors.getAssociateColor(), barzellettaAttuale);
            Integer colorFrom;
            Drawable background = ((Drawable) layoutBarzellette.getBackground());
            if (background instanceof ColorDrawable)
                colorFrom = ((ColorDrawable) background).getColor();
            else colorFrom = 0;

            Integer colorTo = color;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    layoutBarzellette.setBackgroundColor((Integer) animator.getAnimatedValue());
                    nextButton.setTextColor((Integer) animator.getAnimatedValue());
                    precedenteButton.setTextColor((Integer) animator.getAnimatedValue());
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

    private void setBarzelletta(int mode) {
        immagine.resetZoom();
        immagine.setImageBitmap(null);

        if(mode == this.MODE_NEXT) {
            if (lista != null) {
                barzellettaAttuale = lista.getRandom();
            } else {
                barzellettaAttuale = new Barzelletta(-1, "Impossibile caricare barzellette, prova a riavviare l'app", false, Categoria.UNDEFINED, false, Tipo.UNDEFINED);
            }
        }
        else if(mode == this.MODE_PREVIOUS){
            barzellettaAttuale = lista.getBarzellettaPrima();
        }

        isActualFavorite = favoriti.contains(barzellettaAttuale);
        setColor();

        if(barzellettaAttuale.getTipo().getID() == Tipo.TESTO.getID()){
            textView.setVisibility(View.VISIBLE);
            layoutImmagini.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            textView.setText(barzellettaAttuale.toString());
            textView.post(new Runnable() {

                @Override
                public void run() {
                    textView.scrollTo(0, 0);
                }
            });
            fallimentoCaricamento = false;
            inCaricamento=false;
        }

        else{
            textView.setVisibility(View.GONE);
            layoutImmagini.setVisibility(View.VISIBLE);

            impostaImmagine();

        }


        checkBarzelletta();

    }

    private void checkBarzelletta() {
        isActualFavorite=!favoriti.contains(barzellettaAttuale);

        if (isActualFavorite) {
        //    favoriteButton.setColorFilter(android.graphics.Color.parseColor("#741f14"));
            favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_heart_disabled));
        } else {
     //       favoriteButton.setColorFilter(null);
            favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_heart_enabled));
        }

        if(!lastIsPresent()){
            precedenteButton.setEnabled(false);
        }
        else{
            precedenteButton.setEnabled(true);
        }
    }

    private boolean lastIsPresent(){
        if(lista!=null)
          return lista.esisteBarzellettaPrima();
        else
            return false;
    }

    private void abilitaPro(){
        if(versionePro){
            precedenteButton.setVisibility(View.VISIBLE);
            precedenteButton.setEnabled(lista.esisteBarzellettaPrima());
        }
        else{
            setAds();
        }
    }

    private void setAds(){
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layoutInferiore.getLayoutParams();
        params.addRule(RelativeLayout.ABOVE, R.id.adView);
        params.setMargins(0,0,0,0);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        mAdView.setVisibility(View.VISIBLE);
        layoutInferiore.setLayoutParams(params);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if(immagine.getCurrentZoom()==1.0) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Informazioni sulla barzelletta:");
            StringBuilder sb = new StringBuilder();
            sb.append("Categoria : " + barzellettaAttuale.getCategoria().toString().toLowerCase() + "\n");
            if (barzellettaAttuale.isVM()) {
                sb.append("E' per adulti\n");
            } else {
                sb.append("E' per tutti\n");
            }
            if (barzellettaAttuale.isLunga()) {
                sb.append("E' lunga\nID: ");
            } else {
                sb.append("E' breve\nID: ");
            }
            sb.append(barzellettaAttuale.getID());
            alertDialog.setMessage(sb.toString());
            alertDialog.show();
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
           //Do nothing, vertical swipe
        } else {
            if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                return false;
            }
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                        setBarzelletta(MODE_NEXT);
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                if(lastIsPresent())
                    setBarzelletta(MODE_PREVIOUS);
            }
        }
        return false;
    }

    private static Libro filtra(List<Barzelletta> lista, Categoria categoria){
        List<Categoria> listaCategorie = new ArrayList<Categoria>();
        List<Barzelletta> result = new ArrayList<>();

       if(categoria != null && categoria.getID() >=0){
            for (Barzelletta attuale : lista) {
                if (attuale.getCategoria().equals(categoria))
                    result.add(attuale);
            }
        }

       else {

           for (Barzelletta attuale : lista){
               if(!listaCategorie.contains(attuale.getCategoria())){
                   result.add(attuale);
               }
           }

       }

        return new Libro(result);

    }

    private void segnala() {
        DialogSegnala dialogSegnala = new DialogSegnala();
        dialogSegnala.show(getFragmentManager(), "");
    }

    private void proproni() {
        DialogProponi dialogProponi = new DialogProponi();
        dialogProponi.show(getFragmentManager(), "");
    }

    private void impostaImmagine() {

        progressBar.setVisibility(View.VISIBLE);
        inCaricamento=true;
        new DownloadImageTask(immagine)
                .execute(barzellettaAttuale.toString());

    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int idBarzellettaDaCaricare = barzellettaAttuale.getID();

        public DownloadImageTask(ImageView bmImage) {

            this.bmImage = bmImage;


        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result==null){
                if(!fallimentoCaricamento) {
                    fallimentoCaricamento = true;
                    setBarzelletta(MODE_NEXT);
                    Toast.makeText(context, "Impossibile caricare immagine. Controlla la connessione", Toast.LENGTH_SHORT).show();
                }
                else{
                    immagine.setImageResource(R.drawable.ic_offline);
                }
            }
            else {
               if(idBarzellettaDaCaricare == barzellettaAttuale.getID()) {
                   immagineAttuale = result;
                   bmImage.setImageBitmap(immagineAttuale);
                   inCaricamento=false;
               }
                fallimentoCaricamento = false;
            }
            progressBar.setVisibility(View.GONE);
        }
    }


    private void share() {
        Intent i = new Intent(android.content.Intent.ACTION_SEND);


        ParseObject richiesta = ParseObject.create(BARZELLETTA_CONDIVISA_OBJECT_KEY);
        richiesta.put(ID_KEY, barzellettaAttuale.getID());
        richiesta.put(TESTO_KEY, barzellettaAttuale.toString());
        richiesta.put(CATEGORIA_KEY, barzellettaAttuale.getCategoria().toString());
        richiesta.put(VERSIONE_KEY, getString(R.string.application_version));
        richiesta.saveInBackground();


        if(barzellettaAttuale.getTipo().equals(Tipo.TESTO)) {
            i.setType("text/plain");
            i.putExtra(android.content.Intent.EXTRA_TEXT, barzellettaAttuale.toString() + "\n\n Presa da Barzellette a caso, scarica l'applicazione! " + this.getResources().getString(R.string.url_app_playstore));
            startActivity(Intent.createChooser(i, getString(R.string.condividi_con)));
        }
        else{
            OutputStream output;



            // Create a name for the saved image
            File file = new File(getContext().getExternalCacheDir() , "immagine.png");


            try {
                // Share Intent
                Intent share = new Intent(Intent.ACTION_SEND);

                // Type of file to share
                share.setType("image/png");

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    immagineAttuale.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    Log.e("Thumbnail", file.getPath());
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {

                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Locate the image to Share
                Uri uri = Uri.fromFile(file);

                // Pass the image into an Intnet
                share.putExtra(Intent.EXTRA_STREAM, uri);

                // Show the social share chooser list
                startActivity(Intent.createChooser(share, "Condividi immagine con"));

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }

    private void saveThumbnail(Bitmap image){

        int THUMBSIZE = 150;
        // Create a new folder AndroidBegin in SD Card
        // File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/barzelletteAcaso/thumbnail");
        File dir = new File("/data/data/com.matteobucci.barzelletteacaso/thumbnail");
        dir.mkdirs();
        File file = new File(dir, Integer.toString(barzellettaAttuale.getID()) + ".png");
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(image, THUMBSIZE, THUMBSIZE);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
           thumbImage.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            Log.e("Thumbnail", file.getPath());
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {

                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void cancellaThumbnail(){
        if(barzellettaAttuale.getTipo().equals(Tipo.IMMAGINE)){
          //  File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/barzelletteAcaso/thumbnail" + Integer.toString(barzellettaAttuale.getID()) + ".png");
            File file = new File("/data/data/com.matteobucci.barzelletteacaso/thumbnail/" + Integer.toString(barzellettaAttuale.getID()) + ".png");

            boolean deleted = file.delete();

        }
    }

}
