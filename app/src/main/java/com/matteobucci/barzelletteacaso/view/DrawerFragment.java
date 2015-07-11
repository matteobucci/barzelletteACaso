package com.matteobucci.barzelletteacaso.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matteobucci.barzelletteacaso.R;

/**
 * Created by Matti on 10/07/2015.
 */
public class DrawerFragment extends Fragment {

    public DrawerFragment(){};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.drawer_layout, container, false);
        return rootView;

    }


}
