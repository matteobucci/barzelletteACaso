package com.matteobucci.barzelletteacaso;

import android.graphics.Color;

import com.matteobucci.barzelletteacaso.model.Barzelletta;

/**
 * Created by Matti on 11/07/2015.
 */
public interface BarzellettaListener {
    public void onChangeBarzelletta(int color, int darkerColor, Barzelletta barzelletta);

}
