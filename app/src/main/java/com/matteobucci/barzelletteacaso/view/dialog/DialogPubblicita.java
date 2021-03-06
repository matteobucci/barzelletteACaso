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
 * Created by matteo on 25/01/16.
 */
public class DialogPubblicita extends DialogFragment {

    private final String TAG = this.getClass().getSimpleName();
    private MainListener mainListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences("", Context.MODE_PRIVATE).edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Vuoi rimuovere le pubblicità?").setMessage("Con un piccolo contributo non solo toglierai tutte le pubblicità, ma aiuterai l'autore a migliorare questa app e a pagarsi l'università." +
                        "\n"
        ).setPositiveButton("Rimuovi", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                if (mainListener != null) {
                    mainListener.onAzione(1);
                    editor.putBoolean(StatStr.RICHIESTA_RIMOZIONE_PUBBLICITA, false).apply();
                    Log.i("Inserito", Boolean.toString(getActivity().getSharedPreferences("", Context.MODE_PRIVATE).getBoolean(StatStr.RICHIESTA_RIMOZIONE_PUBBLICITA, true)));
                }
            }
        })
                .setNegativeButton("No, grazie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        editor.putBoolean(StatStr.RICHIESTA_RIMOZIONE_PUBBLICITA, false).apply();
                        Log.i("Inserito", Boolean.toString(getActivity().getSharedPreferences("", Context.MODE_PRIVATE).getBoolean(StatStr.RICHIESTA_RIMOZIONE_PUBBLICITA, true)));
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
