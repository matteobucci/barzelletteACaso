package com.matteobucci.barzelletteacaso;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.database.BarzelletteManager;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.Libro;
import com.matteobucci.barzelletteacaso.model.Tipo;

/**
 * Implementation of App Widget functionality.
 */
public class BarzellettaDelGiornoWidget extends AppWidgetProvider {

    private final static String tag = "clickTag";




    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Barzelletta barzelletta = getBarzelletta(context);
        CharSequence testoBarzelletta = barzelletta.toString();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.barzelletta_del_giorno_widget);

        String[] barzellettaDivisa = dividiBarzelletta(barzelletta);
        int parteAttuale=0;



        if(testoBarzelletta==null) {
            views.setTextViewText(R.id.appwidget_text, "Non funziona");
        }
        else{
            views.setTextViewText(R.id.appwidget_text, barzellettaDivisa[0]);
        }



        if(true){
            views.setViewVisibility(R.id.buttonDownWidget, View.INVISIBLE);
          views.setViewVisibility(R.id.buttonUpWidget, View.INVISIBLE);
        }
     //   else{
     //       views.setViewVisibility(R.id.buttonDownWidget, View.VISIBLE);
     //       views.setViewVisibility(R.id.buttonUpWidget, View.VISIBLE);
      //  }

        Intent destIntent = new Intent(context, BarzellettaDelGiorno.class);
        destIntent.putExtra("barzelletta", (Parcelable) barzelletta);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(BarzellettaDelGiorno.class);
        stackBuilder.addNextIntent(destIntent);
        PendingIntent pendingIntentStack = stackBuilder.getPendingIntent(barzelletta.getID(), PendingIntent.FLAG_UPDATE_CURRENT);


     //   PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, destIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntentStack);
    //    views.setOnClickPendingIntent(R.id.buttonDownWidget, getPendingSelfIntent(context, tag));


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static Barzelletta getBarzelletta(Context context){
        Libro libro = new Libro(new BarzelletteManager(context).getAllBarzellette());

        Barzelletta barzelletta = libro.getRandom();
        while(!barzelletta.getTipo().equals(Tipo.TESTO)){
            barzelletta = libro.getRandom();
        }

        return barzelletta;
    }

    private String[] dividiBarzelletta(Barzelletta barzelletta){
        if(barzelletta.isLunga()){
            int DIVISORE = 150;
            int parti = barzelletta.toString().length()/DIVISORE;
            String[] result = new String[parti];
            for(int i=0; i<parti-1; i++){
                result[i] = barzelletta.toString().substring(i*DIVISORE, (i+1)*DIVISORE)+"...";
            }
            result[parti-1] = barzelletta.toString().substring((parti-1)*DIVISORE, (parti)*DIVISORE);
            return result;
        }
        else if(barzelletta.toString().split("\n").length>3){
            int parti = barzelletta.toString().split("\n").length/2;
            String[] result = new String[parti];
            String[] linee = barzelletta.toString().split("\n");
            for(int i=0; i<parti-1; i++){
                result[i] = linee[i*2]+linee[(i*2)+1]+"...";
            }
            if(parti%2==1){
                result[parti-1] = linee[(parti-1)*2];
            }
            else{
                result[parti-1] = linee[(parti-1)*2]+ linee[(parti-1)*2-1];
            }

            return result;
        }

        return new String[]{barzelletta.toString()};
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action){
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent){

        super.onReceive(context, intent);

        if(tag.equals(intent.getAction())){
           ;
        }
    }

}

