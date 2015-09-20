package com.matteobucci.barzelletteacaso.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.matteobucci.barzelletteacaso.model.Favorite;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Categoria;
import com.matteobucci.barzelletteacaso.model.Libro;
import com.matteobucci.barzelletteacaso.model.listener.BarzellettaListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.matteobucci.barzelletteacaso.view.support.AppRater;
import com.matteobucci.barzelletteacaso.view.support.ColorList;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements GestureDetector.OnGestureListener {

    private final String TAG = this.getClass().getSimpleName();
    private TextView  textView;
    private Button nextButton;
    private Button precedenteButton;
    private ImageButton favoriteButton;
    private RelativeLayout layoutBarzellette;
 //   private BarzelletteManager manager;
    private Libro lista;
    private Barzelletta barzellettaAttuale = null;
    private Favorite favoriti;
    private ColorList colors;
    private boolean isActualFavorite;
    private Context context;
    private Categoria categoria;
    private BarzellettaListener colorListener;
    private boolean versionePro;
    private AdView mAdView;
    private AdRequest adRequest;
    private LinearLayout layoutInferiore;
    private GestureDetector myGestDetector;
    private boolean swipeEnabled;
    private boolean appenaAvviata=true;
    boolean sfondoChiaro = false;
    static final int SWIPE_MIN_DISTANCE = 120;
    static final int SWIPE_MAX_OFF_PATH = 250;
    static final int SWIPE_THRESHOLD_VELOCITY = 200;


    private int textSize;

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

        versionePro = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("versione_pro", false);

        favoriti = Favorite.getInstance(context);
        favoriti.loadFavorite();
        AppRater.show(context, getFragmentManager());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_main, container, false);
        setUIVar(myInflatedView);
        setListeners();
        setBarzelletta();
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
        if(favoriti != null){
            favoriti.saveFavorite();
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
                setBarzelletta();
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barzellettaAttuale != null) {
                    if (favoriti.contains(barzellettaAttuale)) {
                        favoriti.remove(barzellettaAttuale);
                        Toast.makeText(context, "Preferito rimosso", Toast.LENGTH_SHORT).show();
                    } else {
                        favoriti.add(barzellettaAttuale);
                        Toast.makeText(context, "Preferito aggiunto", Toast.LENGTH_SHORT).show();
                    }
                    checkBarzelletta();
                }
            }
        });

        precedenteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBarzellettaPrecendete();
            }
        });

        myGestDetector = new GestureDetector(context, this);

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (swipeEnabled) {
                    myGestDetector.onTouchEvent(event);
                }
                return false;
            }
        });


    }

    private void setBarzellettaPrecendete() {
        barzellettaAttuale = lista.getBarzellettaPrima();
        isActualFavorite = favoriti.contains(barzellettaAttuale);
        textView.setText(barzellettaAttuale.toString());
        setColor();
        textView.post(new Runnable(){

            @Override
            public void run()
            {
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

    private void setBarzelletta() {
        if(lista!=null) {
            barzellettaAttuale = lista.getRandom();
        }
        else{
            barzellettaAttuale = new Barzelletta(-1, "Impossibile caricare barzellette, prova a riavviare l'app", false, Categoria.UNDEFINED, false);
        }
        isActualFavorite = favoriti.contains(barzellettaAttuale);
        textView.setText(barzellettaAttuale.toString());
        setColor();
        textView.post(new Runnable(){

            @Override
            public void run()
            {
                textView.scrollTo(0, 0);
            }
        });
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
        return lista.esisteBarzellettaPrima();
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
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Informazioni sulla barzelletta:");
        StringBuilder sb = new StringBuilder();
        sb.append("Categoria : " + barzellettaAttuale.getCategoria().toString().toLowerCase() + "\n");
        if(barzellettaAttuale.isVM()){
            sb.append("E' per adulti\n");
        }
        else{
            sb.append("E' per tutti\n");
        }
        if(barzellettaAttuale.isLunga()){
            sb.append("E' lunga\nID: ");
        }
        else{
            sb.append("E' breve\nID: ");
        }
        sb.append(barzellettaAttuale.getID());
        alertDialog.setMessage(sb.toString());
        alertDialog.show();
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
                        setBarzelletta();
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                if(lastIsPresent())
                    setBarzellettaPrecendete();
            }
        }
        return false;
    }

    private static Libro filtra(List<Barzelletta> lista, Categoria categoria){
        if(categoria == null)
            return new Libro (lista);

        List<Barzelletta> result = new ArrayList<>();
        for (Barzelletta attuale : lista){
            if(attuale.getCategoria().equals(categoria))
                result.add(attuale);
        }

        return new Libro(result);

    }

}
