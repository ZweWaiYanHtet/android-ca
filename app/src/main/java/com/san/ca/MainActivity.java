package com.san.ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    String[] imgUrls = new String[] {
      "https://cdn.stocksnap.io/img-thumbs/280h/IRGQEM4BZE.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/2ANB3L3OFJ.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/INUSYQIPX7.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/YEVGMVHWLN.jpg"
            , "https://cdn.stocksnap.io/img-thumbs/280h/0TX2A6KS0W.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/PIZFAFFQIK.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/X6QBLPBXAJ.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/SJWJRHWNW5.jpg",
            "https://cdn.stocksnap.io/img-thumbs/280h/Y1RQRRLIXJ.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/KE7875OUCR.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/IUFVZUDPO5.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/BHYCPVMIPD.jpg",
            "https://cdn.stocksnap.io/img-thumbs/280h/I5IPUY6JUV.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/CDD6XUMSHJ.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/NJNSUMSS3L.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/BIMCBJJXNJ.jpg",
            "https://cdn.stocksnap.io/img-thumbs/280h/OONZXHIOWK.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/GW5CWPGKDG.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/QYSHSLCODM.jpg", "https://cdn.stocksnap.io/img-thumbs/280h/WP3DISZGSO.jpg"
    };


    List<ImageView> imgViews = Arrays.asList(new ImageView[20]);
    ArrayList<Integer> selectedImgIndexes = new ArrayList<>();
    TextView tvProgress;
    ProgressBar pbProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get all 20 image views and set onclick listener
        for (int i = 0; i < 20; i++) {

            int id = getResources().getIdentifier("iV" + (i + 1), "id", getPackageName());
            ImageView iv = findViewById(id);
            imgViews.set(i, iv);

        }

        tvProgress = findViewById(R.id.tvProgress);
        pbProgress = findViewById(R.id.pbProgress);
        pbProgress.setMax(20);
        loadImages();

    }


    @Override
    public void onClick(View view) {

        for(int i = 0; i < 20; i++) {

            if (imgViews.get(i).getId() == view.getId()) {

                ImageView iv = imgViews.get(i);

                if(!selectedImgIndexes.contains(i)) {

                    selectedImgIndexes.add(i);
                    iv.setBackgroundColor(Color.parseColor("#D81B60"));
                    iv.setPadding(10, 10, 10, 10);

                } else {

                    selectedImgIndexes.remove((Object)i);
                    iv.setBackgroundColor(Color.parseColor("#ffffff"));
                    iv.setPadding(0, 0, 0, 0);

                }
                break;

            }

        }

        if(selectedImgIndexes.size() >= 6) {

            Intent i = new Intent(this, Game.class);
            i.putIntegerArrayListExtra("selectedImgIndexes", selectedImgIndexes);
            startActivity(i);

            clearSelected();

        }

    }

    private void clearSelected() {
        for(int index: selectedImgIndexes) {
            ImageView iv = imgViews.get(index);
            iv.setBackgroundColor(Color.parseColor("#ffffff"));
            iv.setPadding(0, 0, 0, 0);
        }
        selectedImgIndexes.clear();
    }

    private void loadImages() {
        tvProgress.setVisibility(View.VISIBLE);
        pbProgress.setVisibility(View.VISIBLE);
        pbProgress.setProgress(0);
        new ImageDownloadTask().execute(imgUrls);
    }

    private void setOnClickListeners() {
        for(ImageView iv : imgViews) {
            iv.setOnClickListener(this);
        }
    }

    class ImageDownloadTask extends AsyncTask<String, Bitmap, String> {
        int finishedCount = 0;

        Bitmap[] downloadedImages = DownloadedImagesArraySingleton.getInstance();

        protected String doInBackground(String... addresses) {

            for (int i = 0; i < addresses.length; i++) {
                Log.i("Async", "Downloading: " + addresses[i]);
                Bitmap bitmap = null;
                InputStream in = null;
                try {
                    // 1. Declare a URL Connection
                    URL url = new URL(addresses[i]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 2. Open InputStream to connection
                    conn.connect();
                    in = conn.getInputStream();
                    // 3. Download and decode the bitmap using BitmapFactory
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            Log.e("TAG", "Exception while closing inputstream" + e);
                        }
                    }
                }

                publishProgress(bitmap);
            }

            return "Finished";
        }


        protected void onProgressUpdate(Bitmap... imgs) {

            imgViews.get(finishedCount).setImageBitmap(imgs[0]);

            // set the downloaded images in the singleton for later use;
            downloadedImages[finishedCount] = imgs[0];

            finishedCount++;
            tvProgress.setText(finishedCount + " of 20 finished.");
            pbProgress.setProgress(finishedCount);

        }


        // Fires after the task is completed, displaying the bitmap into the ImageView
        @Override
        protected void onPostExecute(String s) {
            // Set bitmap image for the result
            tvProgress.setText("Download Finished! You can start playing now!");
            pbProgress.setVisibility(View.INVISIBLE);
            setOnClickListeners();
        }

    }




}
