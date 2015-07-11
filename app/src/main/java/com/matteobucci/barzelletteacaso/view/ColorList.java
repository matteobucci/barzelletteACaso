package com.matteobucci.barzelletteacaso.view;

/**
 * Created by Matti on 11/07/2015.
 */

import java.util.Random;


public class ColorList {

    public ColorList(){
        gen = new Random();
    }

    private Random gen;

    public String[] mColors = {
            "#39add1", // light blue
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#e15258", // red
            "#f9845b", // orange
            "#838cc7", // lavender
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#b7c0c7"  // light gray
    };

    public int getColor(){
        return android.graphics.Color.parseColor(mColors[gen.nextInt(mColors.length)]);
    }

}
