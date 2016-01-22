package com.matteobucci.barzelletteacaso.view;



import android.os.Bundle;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Favorite;
import com.matteobucci.barzelletteacaso.view.FavoriteFragmentImmagini;
import com.matteobucci.barzelletteacaso.view.FavoriteFragmentTesto;
import com.parse.ParseObject;

public class NewFavoriteFragment extends Fragment {


    private static final String SESSIONE_BARZELLETTE_OBJECT_KEY = "AperturaPreferiti";
    private static final String NUMERO_BARZELLETTE_KEY = "numeroPreferiti";
    Fragment fragment1;
    Fragment fragment2;


    InterstitialAd mInterstitialAd;


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);


        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                //beginPlayingGame();
            }
        });

        requestNewInterstitial();




    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
              //  .addTestDevice("C7167A21A92007A61656EFD10B07F18B")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_new_favorite, container, false);

        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Testo"));
        tabLayout.addTab(tabLayout.newTab().setText("Immagini"));
        final ViewPager viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();



                } else {

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }



        });

        return inflatedView;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    fragment1 =  new FavoriteFragmentTesto();
                    return fragment1;
                case 1:
                    fragment2 =  new FavoriteFragmentImmagini();
                    return fragment2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }


    @Override
    public void onDetach(){
        super.onDetach();
        if(fragment1 != null && fragment2 != null) {
            int numeroPreferiti = Favorite.getInstance(getContext()).getNumeroFavoriti();
            ParseObject richiesta = ParseObject.create(SESSIONE_BARZELLETTE_OBJECT_KEY);
            richiesta.put(NUMERO_BARZELLETTE_KEY, numeroPreferiti);
            richiesta.saveInBackground();

            fragment1.onDetach();
            fragment2.onDetach();
        }
    }
}


