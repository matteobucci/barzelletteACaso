package com.matteobucci.barzelletteacaso.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Favorite;
import com.matteobucci.barzelletteacaso.model.Tipo;
import com.matteobucci.barzelletteacaso.view.support.MyImageAdapter;
import com.matteobucci.barzelletteacaso.view.support.MyListAdapter;

import java.io.File;
import java.util.List;

public class FavoriteFragmentImmagini extends Fragment {


    private final String TAG = this.getClass().getSimpleName();

    //Variabili principali
    private Favorite favoriti;
    private Context context;
    private List<Barzelletta> listaBarzellettePreferite;

    //Variabili della UI
    private RecyclerView recList;
    private TextView emptyView;





    public static FavoriteFragmentImmagini newInstance() {
        return new FavoriteFragmentImmagini();
    }

    public FavoriteFragmentImmagini() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Carica i preferiti
        favoriti = Favorite.getInstance(context);
        favoriti.loadFavorite();
        listaBarzellettePreferite = favoriti.getFavoriteImmagine();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.favorite_fragment, container, false);

        emptyView = (TextView) view.findViewById(R.id.emptyListError);
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        final MyImageAdapter adapter = new MyImageAdapter(listaBarzellettePreferite, context);

        recList.setHasFixedSize(true);
        recList.setLayoutManager(getLayoutManager(context));
        recList.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int posizione;
                posizione = viewHolder.getAdapterPosition();
                int idBarzelletta = adapter.getIDBarzelletta(posizione);
                favoriti.removeByID(idBarzelletta);

                listaBarzellettePreferite.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                checkIfIsVuota();
                final View coordinatorLayoutView = view.findViewById(R.id.snackbarPosition);

                Snackbar
                        .make(coordinatorLayoutView, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                        .show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recList);
        checkIfIsVuota();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(favoriti != null){
            favoriti.saveFavorite();
        }
    }

    private void checkIfIsVuota(){
        if(listaBarzellettePreferite.isEmpty()){
            setEmptyText(context.getString(R.string.no_favorites_list));
        }
        else
            setEmptyText("");
    }

    private void setEmptyText(CharSequence emptyText) {
        emptyView.setText(emptyText);
    }



    private RecyclerView.LayoutManager getLayoutManager(Context context){
        RecyclerView.LayoutManager layoutManager;


            layoutManager = new GridLayoutManager(context, 2);

        return layoutManager;
    }




}
