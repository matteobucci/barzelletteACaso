package com.matteobucci.barzelletteacaso.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.matteobucci.barzelletteacaso.view.support.MyListAdapter;

import java.util.List;

public class FavoriteFragment extends Fragment{

    private static final int AD_FREQUENCY = 4;
    private Favorite favoriti;
    private Context context;
    private List<Barzelletta> listaBarzellettePreferite;
    private TextView emptyView;
    private RecyclerView recList;
    InterstitialAd mInterstitialAd;
    int numeroAvvii;


    // TODO: Rename and change types of parameters
    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriti = Favorite.getInstance(context);
        favoriti.loadFavorite();
        listaBarzellettePreferite = favoriti.getFavoriteList();
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getActivity().getString(R.string.test_banner_ad_unit_id));

        Log.i("Informazione", "OnCreate attivato");
        numeroAvvii = getActivity().getSharedPreferences("", Context.MODE_PRIVATE).getInt("avvii", 0);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("", Context.MODE_PRIVATE).edit();
        numeroAvvii = numeroAvvii%AD_FREQUENCY;
        editor.putInt("avvii", numeroAvvii + 1);
        Log.i("numeroAvvii=", "" + numeroAvvii);
        editor.apply();
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {


        if (numeroAvvii % AD_FREQUENCY == 2) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("7701B3F7E65960162ABAF259B6366C71")
                    .build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();

                }
            });
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.favorite_fragment, container, false);
        emptyView = (TextView) view.findViewById(R.id.emptyListError);
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);


        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);


        //TODO:Migliorare la visualizzazione di colonne
    /*

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int colums = metrics.widthPixels/380;
        if(colums==0) colums = 1;

        GridLayoutManager glm = new GridLayoutManager(context, colums);

        */

        recList.setLayoutManager(llm);


        final MyListAdapter adapter = new MyListAdapter(listaBarzellettePreferite, context);
        recList.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
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



    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
            emptyView.setText(emptyText);
    }

    private void checkIfIsVuota(){
        if(listaBarzellettePreferite.isEmpty()){
            setEmptyText("Non sono state ancora aggiunte barzellette preferite. Perchè non ne aggiungi qualcuna?");
        }
        else
            setEmptyText("");
    }





}
