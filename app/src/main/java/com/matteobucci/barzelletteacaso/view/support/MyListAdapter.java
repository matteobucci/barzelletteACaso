package com.matteobucci.barzelletteacaso.view.support;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;

import java.util.List;

/**
 * Created by Matti on 18/07/2015.
 */

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.BarzellettaViewHolder> {

    private List<Barzelletta> barzellettaList;
    private Context context;

    public MyListAdapter(List<Barzelletta> barzellettaList, Context context) {
        this.barzellettaList = barzellettaList;
        this.context = context;
    }

    @Override
    public BarzellettaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_card_view_layout, parent, false);
        final BarzellettaViewHolder vh = new BarzellettaViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(vh.testoBarzelletta.getText().toString() + context.getString(R.string.testo_condivisione) + context.getString(R.string.url_app_playstore));
            }
        });

        return vh;
    }


    public void share(String testo){
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_TEXT, testo);
        context.startActivity(Intent.createChooser(i, context.getString(R.string.condividi_con_text)));
    }


    @Override
    public void onBindViewHolder(BarzellettaViewHolder holder, int position) {
        holder.testoBarzelletta.setText(barzellettaList.get(position).toString());
        holder.idBarzelletta = barzellettaList.get(position).getID();
    }


    @Override
    public int getItemCount() {
        return barzellettaList.size();
    }

    public int getIDBarzelletta(int position){
        Log.i("fdsoigaosjg" ,"" + position);
        return barzellettaList.get(position).getID();
    }


    public static class BarzellettaViewHolder extends RecyclerView.ViewHolder {

        protected TextView testoBarzelletta;

        protected int idBarzelletta;

        public BarzellettaViewHolder(View v) {
            super(v);
            testoBarzelletta = (TextView) v.findViewById(R.id.text_barzelletta);

        }



    }


}
