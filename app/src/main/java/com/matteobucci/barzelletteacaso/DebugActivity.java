package com.matteobucci.barzelletteacaso;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.matteobucci.barzelletteacaso.view.dialog.DialogProponi;
import com.matteobucci.barzelletteacaso.view.dialog.DialogSegnala;
import com.matteobucci.barzelletteacaso.view.dialog.DialogSuggerimento;

public class DebugActivity extends AppCompatActivity {

    private Button valutaButton;
    private Button proponiButton;
    private Button segnalaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        valutaButton = (Button) findViewById(R.id.dialog_ti_piace_barza_button);
        proponiButton = (Button) findViewById(R.id.dialog_proponi_barzelletta);
        segnalaButton = (Button) findViewById(R.id.dialog_segnala_barza);

        valutaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valuta();
            }
        });

        proponiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proproni();
            }
        });

        segnalaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                segnala();
            }
        });




    }



    private void valuta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ti piace l'applicazione?").setMessage("Ci teniamo tanto al tuo parere, cosa ne pensi?")
                .setPositiveButton("Mi piace!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                        valutazione_positiva();

                    }
                })
                .setNegativeButton("Si può far di meglio!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                        suggerimento();
                    }
                }).show();
    }


    private void valutazione_positiva(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Valutaci nel Play Store")
                                .setMessage("Siamo felici che l'applicazione ti piaccia. Perchè non ci lasci una valutazione positiva nel Play Store?")
                                .setPositiveButton("Va bene", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setNegativeButton("No, grazie.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setNeutralButton("Più tardi", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();

    }


    private void segnala() {
        DialogSegnala dialogSegnala = new DialogSegnala();
        dialogSegnala.show(getSupportFragmentManager(), "");
    }

    private void proproni() {
        DialogProponi dialogProponi = new DialogProponi();
        dialogProponi.show(getSupportFragmentManager(), "");

    }

    private void suggerimento(){
        DialogSuggerimento dialogSuggerimento = new DialogSuggerimento();
        dialogSuggerimento.show(getSupportFragmentManager(), "");
    }


}
