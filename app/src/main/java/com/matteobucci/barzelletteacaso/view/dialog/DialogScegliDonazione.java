package com.matteobucci.barzelletteacaso.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.matteobucci.barzelletteacaso.StatStr;
import com.matteobucci.barzelletteacaso.model.listener.MainListener;

/**
 * Created by matteo on 26/01/16.
 */
public class DialogScegliDonazione extends DialogFragment {


    private final String TAG = this.getClass().getSimpleName();
    private MainListener mainListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences("", Context.MODE_PRIVATE).edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Vuoi rimuovere le pubblicità?").setMessage("" +
                        "Con un piccolo contributo puoi rimuovere completamente ogni pubblicità presente nell'applicazione.\n" +
                        "Se oltre a rimuovere la pubblicità vuoi supportare lo sviluppatore, e permettergli non solo di aggiungere nuove immagini, barzellette e funzioni, ma anche di pagarsi gli studi universitari" +
                        " " +
                        "puoi scegliere la seconda opzione e aggiungere un piccolo contributo. Grazie mille :) "
        ).setPositiveButton("Solo pubblicità", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                if (mainListener != null) {
                    mainListener.onAzione(1);
                }
            }
        })
                .setNegativeButton("Contributo aggiuntivo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mainListener != null) {
                            mainListener.onAzione(2);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Context context = activity;
        try {
            mainListener = (MainListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }

    }



}
