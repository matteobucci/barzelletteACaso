package com.matteobucci.barzelletteacaso.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.listener.AcquistoListener;
import com.parse.ParseObject;

/**
 * Created by Matti on 21/09/2015.
 */
public class DialogSuggerimento extends DialogFragment {

    private final String TAG = this.getClass().getSimpleName();

    private EditText editTextTestoSuggerimento;
    private EditText editTextRecapito;

    private static final String SUGGERIMENTO_OBJECT_KEY = "Suggerimenti";
    private static final String TESTO_KEY = "testo";
    private static final String RECAPITO_KEY= "recapito";
    private static final String VERSIONE_KEY = "versione" ;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_suggerimenti, null);
        builder.setView(v);
        editTextTestoSuggerimento = (EditText) v.findViewById(R.id.input_suggerimento);
        editTextRecapito = (EditText) v.findViewById(R.id.input_recapito);

        builder.setTitle(R.string.suggerimento_title).setPositiveButton(R.string.proponi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String testo = editTextTestoSuggerimento.getText().toString();
                String recapito = editTextRecapito.getText().toString();
                String versione = getActivity().getString(R.string.application_version);

                Log.i(TAG, testo + "\n" + recapito + "\n" + versione);

                if(!testo.isEmpty()) {
                    ParseObject richiesta = ParseObject.create(SUGGERIMENTO_OBJECT_KEY);
                    richiesta.put(TESTO_KEY, testo);
                    richiesta.put(RECAPITO_KEY, recapito);
                    richiesta.put(VERSIONE_KEY, versione);
                    richiesta.saveInBackground();

                    Toast.makeText(getActivity(), R.string.suggerimento_inviato, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), R.string.no_testo_in_suggerimento, Toast.LENGTH_SHORT).show();
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
