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
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.R;
import com.parse.ParseObject;

/**
 * Created by Matti on 21/09/2015.
 */
public class DialogSuggerimento extends DialogFragment {

    private EditText editTextTestoSuggerimento;
    private EditText editTextRecapito;

    private static final String SUGGERIMENTO_OBJECT_KEY = "Suggerimenti";
    private static final String TESTO_KEY = "testo";
    private static final String RECAPITO_KEY= "recapito";
    private static final String VERSIONE_KEY = "versione" ;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_suggerimenti, null);
        builder.setView(v);
        editTextTestoSuggerimento = (EditText) v.findViewById(R.id.input_suggerimento);
        editTextRecapito = (EditText) v.findViewById(R.id.input_recapito);

        builder.setTitle("Suggerimento").setPositiveButton(R.string.proponi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String testo = editTextTestoSuggerimento.getText().toString();
                String recapito = editTextRecapito.getText().toString();
                String versione = getActivity().getString(R.string.application_version);

                Log.i("SUGGERIMENTO", testo + "\n" + recapito + "\n" + versione);

                if(!testo.isEmpty()) {
                    ParseObject richiesta = ParseObject.create(SUGGERIMENTO_OBJECT_KEY);
                    richiesta.put(TESTO_KEY, testo);
                    richiesta.put(RECAPITO_KEY, recapito);
                    richiesta.put(VERSIONE_KEY, versione);
                    richiesta.saveInBackground();

                    Toast.makeText(getActivity(), "Suggerimento inviato. Grazie!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Scrivi qualcosa per inviare una proposta", Toast.LENGTH_SHORT).show();
                }


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
