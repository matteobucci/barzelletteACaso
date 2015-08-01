package com.matteobucci.barzelletteacaso.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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
import com.matteobucci.barzelletteacaso.database.BarzelletteManager;
import com.matteobucci.barzelletteacaso.model.listener.BarzellettaListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class FragmentMain extends Fragment {

    private TextView  textView;
    private Button nextButton;
    private Button precedenteButton;
    private ImageButton favoriteButton;
    private RelativeLayout layoutBarzellette;
    private BarzelletteManager manager;
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


    public static FragmentMain newInstance(Categoria categoria) {

        FragmentMain fragment = new FragmentMain();
        fragment.categoria = categoria;
        return fragment;
    }

    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        versionePro = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("versione_pro", false);
        manager = new BarzelletteManager(context);
        if(categoria == null){
            lista= new Libro(manager.getAllBarzellette());
        }
        else {
            lista = new Libro(manager.getBarzellettaByCategoria(categoria));
        }
        favoriti = Favorite.getInstance(context);
        favoriti.loadFavorite();


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
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(favoriti != null){
            favoriti.saveFavorite();
        }
        manager.close();
    }

    @Override
    public void onStart(){
        super.onStart();
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
        setColor();
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
                nextButton.setTextColor((Integer)animator.getAnimatedValue());
                precedenteButton.setTextColor((Integer)animator.getAnimatedValue());
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

    private void setBarzelletta() {
        barzellettaAttuale = lista.getRandom();
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
            favoriteButton.setColorFilter(android.graphics.Color.parseColor("#741f14"));
        } else {
            favoriteButton.setColorFilter(null);
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

    private void animateBackground(int color){

    }

}
