package com.matteobucci.barzelletteacaso.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.matteobucci.barzelletteacaso.model.listener.MainListener;

/**
 * Created by matteo on 2/25/16.
 */
public class DialogMoreImmagini extends DialogFragment {




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Immagini a caso").setMessage("Scarica un app con tutte le immagini di questa applicazione, più tantissime altre! Le migliori immagini che circolano in rete selezionate per voi. Completamente gratis"
        ).setPositiveButton("Va bene!", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

            }
        })
                .setNegativeButton("No, mi accontento!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {



                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


}
