package com.matteobucci.barzelletteacaso.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

public class FragmentMain extends Fragment {

    private TextView textView;
    private Button nextButton;
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



    private void setUIVar(View w) {
        textView = (TextView) w.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        nextButton = (Button) w.findViewById(R.id.nextButton);
        favoriteButton = (ImageButton) w.findViewById(R.id.favoriteButton);
        layoutBarzellette = (RelativeLayout) w.findViewById(R.id.layoutBarzellette);
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

    }

    private void setColor() {
        int color = colors.getColor();
        layoutBarzellette.setBackgroundColor(color);
        colorListener.onChangeBarzelletta(color, colors.getAssociateColor(), barzellettaAttuale);
        nextButton.setTextColor(color);
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
    }

}
