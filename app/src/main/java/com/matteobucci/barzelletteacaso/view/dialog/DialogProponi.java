package com.matteobucci.barzelletteacaso.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.matteobucci.barzelletteacaso.R;

/**
 * Created by Matti on 21/09/2015.
 */
public class DialogProponi extends DialogFragment {

    private EditText editTextTestoBarzelletta;
    private EditText editTextRecapito;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_proponi, null);
        builder.setView(v);
        editTextTestoBarzelletta = (EditText) v.findViewById(R.id.input_barzelletta);
        editTextRecapito = (EditText) v.findViewById(R.id.input_recapito);

        builder.setTitle("Proponi una barzelletta").setPositiveButton(R.string.proponi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String testo = editTextTestoBarzelletta.getText().toString();
                String recapito = editTextRecapito.getText().toString();
                String versione = getActivity().getString(R.string.application_version);

                Log.i("PROPOSTA", testo + "\n" + recapito + "\n" + versione);



            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
