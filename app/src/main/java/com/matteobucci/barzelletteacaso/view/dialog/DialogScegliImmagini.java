package com.matteobucci.barzelletteacaso.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.view.SettingsActivity;
import com.parse.ParseObject;

/**
 * Created by matteo on 17/11/15.
 */
public class DialogScegliImmagini extends DialogFragment {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences("", Context.MODE_PRIVATE).edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Ti interessano le immagini?").setMessage("Ti interessano solo le barzellette o anche le immagini divertenti?" +
                        "\nAttenzione però: per visualizzare le immagini è richiesta una connesione internet."
        ).setPositiveButton("Anche le immagini!", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                //editor.putBoolean(SettingsActivity.immagineString, true);

            }
        })
                .setNegativeButton("Solo barzellette", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       // editor.putBoolean(SettingsActivity.immagineString, false);
                        Intent i = new Intent(getContext(), SettingsActivity.class);
                        startActivity(i);
                    }
                });

        return builder.create();
    }

}
