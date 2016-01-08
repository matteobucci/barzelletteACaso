package com.matteobucci.barzelletteacaso.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
public class DialogSegnala extends DialogFragment {

    private final String TAG = this.getClass().getSimpleName();

    private EditText editTextIDBarzelletta;
    private EditText editTextRecapito;
    private EditText editTextMotivazione;

    private static final String SEGNALA_OBJECT_KEY = "BarzelletteSegnalate";
    private static final String ID_KEY = "idBarzellette";
    private static final String MOTIVO_KEY = "motivo";
    private static final String RECAPITO_KEY= "recapito";
    private static final String VERSIONE_KEY = "versione" ;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_segnala, null);
        builder.setView(v);
        editTextIDBarzelletta = (EditText) v.findViewById(R.id.input_id);
        editTextRecapito = (EditText) v.findViewById(R.id.input_recapito);
        editTextMotivazione = (EditText) v.findViewById(R.id.input_motivo);
      //  TextInputLayout layoutBarzelletta = (TextInputLayout) v.findViewById(R.id.input_layout_id);


        builder.setTitle(R.string.segnala_barzelletta_title).setPositiveButton(R.string.segnala, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String idBarzelletta  = editTextIDBarzelletta.getText().toString();
                String recapito = editTextRecapito.getText().toString();
                String motivo = editTextMotivazione.getText().toString();
                String versione = getActivity().getString(R.string.application_version);

                Log.i(TAG, idBarzelletta + "\n" + motivo + "\n" + recapito + "\n" + versione);
                if(!idBarzelletta.isEmpty()) {

                    ParseObject richiesta = ParseObject.create(SEGNALA_OBJECT_KEY);
                    richiesta.put(ID_KEY, idBarzelletta);
                    richiesta.put(MOTIVO_KEY, motivo);
                    richiesta.put(RECAPITO_KEY, recapito);
                    richiesta.put(VERSIONE_KEY, versione);
                    richiesta.saveInBackground();

                    Toast.makeText(getActivity(), R.string.barzelletta_segnalata, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), R.string.no_testo_barzelletta_segnalata, Toast.LENGTH_SHORT).show();
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
