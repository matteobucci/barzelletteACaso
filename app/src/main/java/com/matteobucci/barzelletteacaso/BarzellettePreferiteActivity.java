package com.matteobucci.barzelletteacaso;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.model.Tipo;
import com.matteobucci.barzelletteacaso.model.TouchImageView;
import com.parse.ParseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BarzellettePreferiteActivity extends AppCompatActivity {

    public static final String INTENT_URL = "urlImmagine";

    private String urlImmagine;
    private ImageButton shareButton;
    private ImageView immagineMostrata;
    private ProgressBar progressBar;
    private boolean inCaricamento = true;
    private Bitmap bitmapImmagine;
    private final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barzellette_preferite);
        urlImmagine = getIntent().getStringExtra(INTENT_URL);
        setUI();
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inCaricamento) {
                    share();
                }
            }
        });

        impostaImmagine();

    }

    private void setUI() {
        shareButton = (ImageButton) findViewById(R.id.shareButtonPreferito);
        immagineMostrata = (ImageView) findViewById(R.id.imageViewPreferito);
        progressBar = (ProgressBar) findViewById(R.id.progressBarPreferito);
    }

    private void impostaImmagine() {
        new DownloadImageTask(immagineMostrata)
                .execute(urlImmagine);

    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result==null){
                    Toast.makeText(context, "Impossibile caricare immagine. Controlla la connessione", Toast.LENGTH_SHORT).show();
            }
            else {
                    bmImage.setVisibility(View.VISIBLE);
                    bitmapImmagine = result;
                    bmImage.setImageBitmap(bitmapImmagine);
                    inCaricamento=false;
                }
            progressBar.setVisibility(View.GONE);
            }

        }




    private void share() {
        Intent i = new Intent(android.content.Intent.ACTION_SEND);


        OutputStream output;



        // Create a name for the saved image
        File file = new File(context.getExternalCacheDir() , "immagine.png");


        try {
            // Share Intent
            Intent share = new Intent(Intent.ACTION_SEND);

            // Type of file to share
            share.setType("image/png");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmapImmagine.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                Log.e("Thumbnail", file.getPath());
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {

                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Locate the image to Share
            Uri uri = Uri.fromFile(file);

            // Pass the image into an Intnet
            share.putExtra(Intent.EXTRA_STREAM, uri);

            // Show the social share chooser list
            startActivity(Intent.createChooser(share, "Condividi immagine con"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }



}
