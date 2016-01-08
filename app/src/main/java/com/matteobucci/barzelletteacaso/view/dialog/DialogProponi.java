package com.matteobucci.barzelletteacaso.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
public class DialogProponi extends DialogFragment {

    private final String TAG = this.getClass().getSimpleName();

    private EditText editTextTestoBarzelletta;
    private EditText editTextRecapito;

    private static final String PROPONI_OBJECT_KEY = "BarzelletteProposte";
    private static final String TESTO_KEY = "testo";
    private static final String RECAPITO_KEY= "recapito";
    private static final String VERSIONE_KEY = "versione" ;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_proponi, null);
        builder.setView(v);
        editTextTestoBarzelletta = (EditText) v.findViewById(R.id.input_barzelletta);
        editTextRecapito = (EditText) v.findViewById(R.id.input_recapito);

        builder.setTitle(R.string.proponi_barzelletta_title).setPositiveButton(R.string.proponi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String testo = editTextTestoBarzelletta.getText().toString();
                String recapito = editTextRecapito.getText().toString();
                String versione = getActivity().getString(R.string.application_version);

                Log.i(TAG, testo + "\n" + recapito + "\n" + versione);
                if(!testo.isEmpty()) {
                    ParseObject richiesta = ParseObject.create(PROPONI_OBJECT_KEY);
                    richiesta.put(TESTO_KEY, testo);
                    richiesta.put(RECAPITO_KEY, recapito);
                    richiesta.put(VERSIONE_KEY, versione);
                    richiesta.saveInBackground();
                    Toast.makeText(getActivity(), R.string.proposta_inviata, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), R.string.no_testo_in_proposta, Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }

}
