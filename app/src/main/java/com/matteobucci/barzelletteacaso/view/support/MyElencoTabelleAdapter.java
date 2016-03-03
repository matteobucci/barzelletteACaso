package com.matteobucci.barzelletteacaso.view.support;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.TabellaBarzellette;

import java.util.List;

/**
 * Created by matteo on 05/02/16.
 */
public class MyElencoTabelleAdapter extends RecyclerView.Adapter<MyElencoTabelleAdapter.ElencoTabelleViewHolder> {

    private List<TabellaBarzellette> tabellaList;
    private Context context;
    private ListTabelleListener listener;

    public MyElencoTabelleAdapter(List<TabellaBarzellette> tabellaList, Context context, ListTabelleListener listener) {
        this.tabellaList = tabellaList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ElencoTabelleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tabellebarzellette, parent, false);
        final ElencoTabelleViewHolder vh = new ElencoTabelleViewHolder(v, new ElencoTabelleViewHolder.MyViewHolderClicks() {
            @Override
            public void onCategoria(String nomeInCodiceTabella) {
                listener.onTabellaScelta(nomeInCodiceTabella);
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
    public void onBindViewHolder(ElencoTabelleViewHolder holder, int position) {
        holder.nomeTabella.setText(tabellaList.get(position).getNomeVisualizzato());
        holder.idTabellaText.setText("Numero elementi: " + tabellaList.get(position).getNumeroBarzellette());
        holder.descrizione.setText(tabellaList.get(position).getDescrizione());
        holder.nomeInCodiceTabella = tabellaList.get(position).getNome();

        holder.idTabella = tabellaList.get(position).getId();
    }


    @Override
    public int getItemCount() {
        return tabellaList.size();
    }

    public int getIDTabella(int position){
        Log.i("fdsoigaosjg", "" + position);
        return tabellaList.get(position).getId();
    }



    public static class ElencoTabelleViewHolder extends RecyclerView.ViewHolder {

        protected TextView nomeTabella;
        protected TextView idTabellaText;
        protected TextView descrizione;
        public MyViewHolderClicks mListener;
        protected String nomeInCodiceTabella;


        protected int idTabella;

        public ElencoTabelleViewHolder(View v, MyViewHolderClicks listener) {
            super(v);
            nomeTabella = (TextView) v.findViewById(R.id.nome_tabella);
            idTabellaText = (TextView) v.findViewById(R.id.numero_barzellette_tabella);
            descrizione = (TextView) v.findViewById(R.id.descrizione_barzellette_tabella);
            mListener = listener;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCategoria(nomeInCodiceTabella);
                }
            });
        }

        public interface MyViewHolderClicks {
             void onCategoria(String nomeInCodiceTabella);
        }




    }


}
