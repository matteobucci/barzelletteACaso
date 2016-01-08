package com.matteobucci.barzelletteacaso.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.matteobucci.barzelletteacaso.R;
import com.parse.ParseObject;

/**
 * Created by Matti on 12/08/2015.
 */

public class AppRater extends DialogFragment {
    private static final int LAUNCHES_UNTIL_PROMPT =2;
    private static final int DAYS_UNTIL_PROMPT = 1;
    private static final long MILLIS_UNTIL_PROMPT = DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000;
    private static final String PREF_NAME = "APP_RATER";
    private static final String LAST_PROMPT = "LAST_PROMPT";
    private static final String LAUNCHES = "LAUNCHES";
    private static final String DISABLED = "DISABLED";

    public static void show(Context context, FragmentManager fragmentManager) {
        boolean shouldShow = false;
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long currentTime = System.currentTimeMillis();
        long lastPromptTime = sharedPreferences.getLong(LAST_PROMPT, 0);
        if (lastPromptTime == 0) {
            lastPromptTime = currentTime;
            editor.putLong(LAST_PROMPT, lastPromptTime);
        }

        if (!sharedPreferences.getBoolean(DISABLED, false)) {
           int launches;
            try {
                launches = sharedPreferences.getInt(LAUNCHES, 0) + 1;
            }
            catch(ClassCastException e){
                //Siccome ogni tanto l'app crasha per questa eccezione, la cattura e la segnala a Parse
                ParseObject richiesta = ParseObject.create("ClassCastException");
                richiesta.put("versione", context.getResources().getString(R.string.application_version));
                richiesta.saveInBackground();
                launches = 0;

            }

            if (launches > LAUNCHES_UNTIL_PROMPT) {
                if (currentTime > lastPromptTime + MILLIS_UNTIL_PROMPT) {
                    shouldShow = true;
                }
            }
            editor.putInt(LAUNCHES, launches);
        }

        if (shouldShow) {
            editor.putInt(LAUNCHES, 0).putLong(LAST_PROMPT, System.currentTimeMillis()).apply();
            new AppRater().show(fragmentManager, null);
        } else {
            editor.apply();
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.ti_piace_applicazione)
                .setMessage(R.string.ti_piace_message)
                .setPositiveButton(R.string.ti_piace_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        valutazione_positiva(getActivity(), getActivity());
                        dismiss();


                    }
                })
                .setNegativeButton(R.string.non_mi_piace, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).commit();
                        dismiss();
                        suggerimento();


                    }
                }).create();
    }

    private void suggerimento(){
        DialogSuggerimento dialogSuggerimento = new DialogSuggerimento();
        dialogSuggerimento.show(getFragmentManager(), "");
    }

    private void valutazione_positiva(Context context, final Activity activity){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(R.string.valutaci_title)
                .setMessage(R.string.valutaci_text)
                .setPositiveButton(R.string.va_bene, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSharedPreferences(activity).edit().putBoolean(DISABLED, true).commit();
                        goToPlayStore(activity);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.no_grazie, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSharedPreferences(activity).edit().putBoolean(DISABLED, true).commit();
                        dismiss();

                    }
                })
                .setNeutralButton(R.string.piu_tardi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSharedPreferences(activity).edit().putLong(LAUNCHES, 0).commit();
                    }
                }).show();

    }

    private void goToPlayStore(Activity activity) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.url_app_playstore))));
    }


}