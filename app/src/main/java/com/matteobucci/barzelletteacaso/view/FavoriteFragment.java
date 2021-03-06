package com.matteobucci.barzelletteacaso.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Favorite;
import com.matteobucci.barzelletteacaso.view.support.MyListAdapter;

import java.util.List;

public class FavoriteFragment extends Fragment{


    private final String TAG = this.getClass().getSimpleName();

    //Variabili principali
    private Favorite favoriti;
    private Context context;
    private List<Barzelletta> listaBarzellettePreferite;

    //Variabili della UI
    private RecyclerView recList;
    private TextView emptyView;


    //Variabili degli annunci
    private InterstitialAd mInterstitialAd;
    private int numeroAvvii;
    private static final int AD_FREQUENCY = 4;
    public static final String AVVII_KEY = "avvii";


    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Carica i preferiti
        favoriti = Favorite.getInstance(context);
        favoriti.loadFavorite();
        listaBarzellettePreferite = favoriti.getFavoriteList();

        //Sceglie se caricare la pubblicità in base al numero di avvii
        numeroAvvii = getActivity().getSharedPreferences("", Context.MODE_PRIVATE).getInt(AVVII_KEY, 0);
        numeroAvvii = numeroAvvii%AD_FREQUENCY;
        Log.i(TAG, "Numero avvii: " + numeroAvvii);
        getActivity().getSharedPreferences("", Context.MODE_PRIVATE).edit().putInt(AVVII_KEY, numeroAvvii + 1).apply();
        if (numeroAvvii % AD_FREQUENCY == 2) {
            mInterstitialAd = new InterstitialAd(getActivity());
         //   mInterstitialAd.setAdUnitId(getActivity().getString(R.string.test_banner_ad_unit_id));
            mInterstitialAd.setAdUnitId(getActivity().getString(R.string.banner_ad_unit_id));
            requestNewInterstitial();
        }
    }

    private void requestNewInterstitial() {
            //AdRequest adRequest = new AdRequest.Builder()
        //
           //         .build();
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("7701B3F7E65960162ABAF259B6366C71").build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if (mInterstitialAd.isLoaded()) { mInterstitialAd.show(); }
                }
                @Override
                public void onAdClosed() { super.onAdClosed(); }
            });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.favorite_fragment, container, false);

        emptyView = (TextView) view.findViewById(R.id.emptyListError);
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        final MyListAdapter adapter = new MyListAdapter(listaBarzellettePreferite, context);

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

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            layoutManager = new GridLayoutManager(context, 2);
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            layoutManager = new GridLayoutManager(context, 3);
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            layoutManager = new LinearLayoutManager(context);
            ((LinearLayoutManager)layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            layoutManager = new LinearLayoutManager(context);
            ((LinearLayoutManager)layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        }
        else {
            layoutManager = new LinearLayoutManager(context);
            ((LinearLayoutManager)layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        }
        return layoutManager;
    }


}
