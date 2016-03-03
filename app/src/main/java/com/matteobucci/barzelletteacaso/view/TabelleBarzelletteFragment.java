package com.matteobucci.barzelletteacaso.view;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.StatStr;
import com.matteobucci.barzelletteacaso.database.GestoreOnline;
import com.matteobucci.barzelletteacaso.model.TabellaBarzellette;
import com.matteobucci.barzelletteacaso.view.dialog.DialogMoreImmagini;
import com.matteobucci.barzelletteacaso.view.dialog.DialogPubblicita;
import com.matteobucci.barzelletteacaso.view.support.ListTabelleListener;
import com.matteobucci.barzelletteacaso.view.support.MyElencoTabelleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link CategoriaOnlineScelta}
 * interface.
 */
public class TabelleBarzelletteFragment extends Fragment implements GestoreOnline.ResultTabelleListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private CategoriaOnlineScelta mListener;
    private List<TabellaBarzellette> listaTabelle;
    private  RecyclerView.Adapter adapter;


    private LinearLayout layoutErrore;
    private Button buttonRicarica;
    private ProgressBar progressBar;
    private GestoreOnline gestore;
    private Button buttonMoreImmagini;

    InterstitialAd mInterstitialAd;

    public TabelleBarzelletteFragment() {
    }


    public static TabelleBarzelletteFragment newInstance() {
        TabelleBarzelletteFragment fragment = new TabelleBarzelletteFragment();
        Bundle args = new Bundle();
        //Aggiungere argomenti se devono essere passati al fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaTabelle = new ArrayList<TabellaBarzellette>();
        if (getArguments() != null) {
            //Se ci sono degli argomenti possono essere recuperati da qua
        }

        gestore = new GestoreOnline(getContext(), null, this);
        gestore.getTabelle();


        if (!PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getBoolean(StatStr.VERSIONE_PRO, false)) {

            mInterstitialAd = new InterstitialAd(getContext());
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_immagini_online_ad_unit_ad));

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {

                    if (getActivity() != null && getActivity().getSharedPreferences("", Context.MODE_PRIVATE).getBoolean(StatStr.RICHIESTA_RIMOZIONE_PUBBLICITA, true)) {

                        DialogFragment loadingDialog = new DialogPubblicita();

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.add(loadingDialog, "loading");
                        transaction.commitAllowingStateLoss();


                      //   DialogPubblicita dialogPubblicità = new DialogPubblicita();
                      //   dialogPubblicità.show(getFragmentManager(), "");
                    }

                }

                @Override
                public void onAdLoaded() {
                    // Call displayInterstitial() function

                }

            });

            requestNewInterstitial();
        }

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C7167A21A92007A61656EFD10B07F18B")
                .build();


        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabellebarzellette_list, container, false);
        layoutErrore = (LinearLayout) view.findViewById(R.id.layoutErrore);
        buttonRicarica = (Button) view.findViewById(R.id.buttonrICARICA);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarTabelle);
        buttonMoreImmagini = (Button) view.findViewById(R.id.buttonMoreImmagini);

        // Set the adapter
        if (true || view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter =  new MyElencoTabelleAdapter(listaTabelle, context, new ListTabelleListener() {
                @Override
                public void onTabellaScelta(String tabellaScelta) {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    mListener.onTabellaScelta(tabellaScelta);
                }
            });
            recyclerView.setAdapter(adapter);


        }

        buttonRicarica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutErrore.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                gestore.getTabelle();
            }
        });
        buttonMoreImmagini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment loadingDialog = new DialogMoreImmagini();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(loadingDialog, "loading");
                transaction.commitAllowingStateLoss();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CategoriaOnlineScelta) {

            mListener = (CategoriaOnlineScelta) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CategoriaOnlineScelta");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResult(List<TabellaBarzellette> listaTabelle) {
        if(listaTabelle !=null && adapter!=null) {
            for (TabellaBarzellette attuale : listaTabelle) {
                this.listaTabelle.add(attuale);
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
        if(listaTabelle== null || listaTabelle.isEmpty()){
            layoutErrore.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

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


    public interface CategoriaOnlineScelta {
        void onTabellaScelta(String tabella);

    }
}
