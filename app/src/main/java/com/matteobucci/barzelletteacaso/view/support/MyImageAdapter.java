package com.matteobucci.barzelletteacaso.view.support;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.matteobucci.barzelletteacaso.BarzellettePreferiteActivity;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;

import java.io.File;
import java.util.List;

/**
 * Created by matteo on 18/11/15.
 */
public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.BarzellettaViewHolder> {

    private List<Barzelletta> barzellettaList;
    private Context context;
   // private String thumbnailPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/barzelletteAcaso/thumbnail/";
    private String thumbnailPath = "/data/data/com.matteobucci.barzelletteacaso/thumbnail/";

    public MyImageAdapter(List<Barzelletta> barzellettaList, Context context) {
        this.barzellettaList = barzellettaList;
        this.context = context;
    }

    @Override
    public BarzellettaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_image_card_view_layout, parent, false);
        final BarzellettaViewHolder vh = new BarzellettaViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BarzellettePreferiteActivity.class);
                intent.putExtra(BarzellettePreferiteActivity.INTENT_URL, vh.URLBarza);
                context.startActivity(intent);
                //share(vh.testoBarzelletta.getText().toString() + context.getString(R.string.testo_condivisione) + context.getString(R.string.url_app_playstore));

            }
        });

        return vh;
    }


    public void share(String testo) {
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_TEXT, testo);
        context.startActivity(Intent.createChooser(i, context.getString(R.string.condividi_con_text)));
    }


    @Override
    public void onBindViewHolder(BarzellettaViewHolder holder, int position) {
        int idBarza =  barzellettaList.get(position).getID();
        holder.URLBarza = barzellettaList.get(position).toString();
        holder.idBarzelletta = idBarza;
        File file = new File(thumbnailPath + idBarza + ".png" );
        Uri uri = Uri.fromFile(file);
        holder.immagineBarzelletta.setImageURI(uri);

    }


    @Override
    public int getItemCount() {
        return barzellettaList.size();
    }

    public int getIDBarzelletta(int position) {
        Log.i("fdsoigaosjg", "" + position);
        return barzellettaList.get(position).getID();
    }


    public static class BarzellettaViewHolder extends RecyclerView.ViewHolder {

        protected int idBarzelletta;
        protected ImageView immagineBarzelletta;
        protected String URLBarza;

        public BarzellettaViewHolder(View v) {
            super(v);
            immagineBarzelletta = (ImageView) v.findViewById(R.id.imageViewThumbnail);
        }


    }

}
