package com.matteobucci.barzelletteacaso.view.support;

/**
 * Created by Matti on 11/07/2015.
 * Lista di colori utilizzati nell'applicazione. Sono disponibili anche i colori più scuri per poter avere un UI più gradevole.
 */

import android.graphics.Color;

import java.util.Random;


public class ColorList {

    public ColorList(){
        gen = new Random();
    }

    private Random gen;
    private int currentColorIndex = -1; //Indice dell'array del colore che è stato dato

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

    public String[] associateColors = {
            "#067A9E", // light blue
            "#004678", // dark blue
            "#8F2642", // mauve
            "#AE1F25", // red
            "#C65128", // orange
            "#505994", // lavender
            "#4A336B", // purple
            "#208881", // aqua
            "#1E813A", // green
            "#AD7800", // mustard
            "#30475E", // dark gray
            "#BD5F7D", // pink
            "#848D94"  // light gray
    };

    public int getColor(){
        currentColorIndex = gen.nextInt(mColors.length);
        return android.graphics.Color.parseColor(mColors[currentColorIndex]);
    }

    public int getAssociateColor(){  //Restituisce il colore associato con quello che ha appena dato.
        if (currentColorIndex == -1){
            return Color.BLACK;
        }
        else return Color.parseColor(associateColors[currentColorIndex]);
    }

}
