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
public class DialogSegnala extends DialogFragment {

    private EditText editTextIDBarzelletta;
    private EditText editTextRecapito;
    private EditText editTextMotivazione;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_segnala, null);
        builder.setView(v);
        editTextIDBarzelletta = (EditText) v.findViewById(R.id.input_id);
        editTextRecapito = (EditText) v.findViewById(R.id.input_recapito);
        editTextMotivazione = (EditText) v.findViewById(R.id.input_motivo);

        builder.setTitle("Segnala una barzelletta").setPositiveButton(R.string.segnala, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String testo = editTextIDBarzelletta.getText().toString();
                String recapito = editTextRecapito.getText().toString();
                String motivo = editTextMotivazione.getText().toString();
                String versione = getActivity().getString(R.string.application_version);

                Log.i("SEGNALA ", testo + "\n" + motivo + "\n" + recapito + "\n" + versione);

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
