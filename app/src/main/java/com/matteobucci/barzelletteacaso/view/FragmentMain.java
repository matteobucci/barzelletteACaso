package com.matteobucci.barzelletteacaso.view;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMain.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMain extends Fragment {


    private TextView textView;
    private Button nextButton;
    private ImageButton favoriteButton;
    private RelativeLayout layoutBarzellette;
   // Button shareButton;

    //Variabili del model
    private BarzelletteManager manager;
    private Libro lista;

    //Variabili che servono a gestire il programma
    Barzelletta barzellettaAttuale = null;
    Favorite favoriti;
    ColorList colors;

    //La barzelleta attuale è favorita?
    private boolean isActualFavorite;

    Context context;
    Categoria categoria;

    private OnFragmentInteractionListener mListener;
    private BarzellettaListener colorListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMain newInstance(Context context, Categoria categoria) {

        FragmentMain fragment = new FragmentMain();
        fragment.context = context;
        fragment.categoria = categoria;
        return fragment;
    }
    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_main, container, false);
        setUIVar(myInflatedView);
        setListeners();
        manager = new BarzelletteManager(context);
        if(categoria == null){
            lista= new Libro(manager.getAllBarzellette());
        }
        else {
            lista = new Libro(manager.getBarzellettaByCategoria(categoria));
        }
        favoriti = Favorite.getInstance(context);
        favoriti.loadFavorite();
        setBarzelletta();

        return myInflatedView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            colorListener = (BarzellettaListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if(favoriti != null){
            favoriti.saveFavorite();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void setUIVar(View w) {
        textView = (TextView) w.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        nextButton = (Button) w.findViewById(R.id.nextButton);
        favoriteButton = (ImageButton) w.findViewById(R.id.favoriteButton);
        layoutBarzellette = (RelativeLayout) w.findViewById(R.id.layoutBarzellette);
        colors = new ColorList();
        setColor();
        //shareButton = (Button) w.findViewById(R.id.action_share);
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
        checkBarzelletta();
    }

    private void checkBarzelletta() {
        if (!favoriti.contains(barzellettaAttuale)) {
            favoriteButton.setColorFilter(android.graphics.Color.parseColor("#741f14"));
        } else {
            favoriteButton.setColorFilter(null);
        }
    }

}
