package com.san.ca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class Game extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Integer> selectedImgIndexes;
    ArrayList<Integer> shuffleList = new ArrayList<>();
    List<ImageView> imgViews = Arrays.asList(new ImageView[12]);
    Bitmap[] downloadedImages = DownloadedImagesArraySingleton.getInstance();

    TextView tvTimer;
    long startTime, timeInMilliseconds = 0;
    Handler customHandler = new Handler();

    TextView tvMatches;
    final int UNSELECTED = -1;
    int selectedIndex = UNSELECTED;
    int matchCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvTimer = findViewById(R.id.tvTimer);
        tvMatches = findViewById(R.id.tvMatches);

        // Get all image views and set onclick listener
        for (int i = 0; i < 12; i++) {
            int id = getResources().getIdentifier("iV" + (i + 1), "id", getPackageName());
            ImageView iv = findViewById(id);
            iv.setOnClickListener(this);
            imgViews.set(i, iv);
        }

        Bundle extras = getIntent().getExtras();

        selectedImgIndexes = extras.getIntegerArrayList("selectedImgIndexes");

        for (int i = 0; i < selectedImgIndexes.size(); i++) {
            shuffleList.add(selectedImgIndexes.get(i));
            shuffleList.add(selectedImgIndexes.get(i));
        }

        Collections.shuffle(shuffleList);

        startTimer();

    }

    @Override
    public void onClick(View view) {

        for (int i = 0; i < imgViews.size(); i++) {

            if (imgViews.get(i).getId() == view.getId()) {

                imgViews.get(i).setImageBitmap(downloadedImages[shuffleList.get(i)]);

                if (selectedIndex == UNSELECTED) {
                    selectedIndex = i;
                }
                else if(selectedIndex == i) {
                    // do nothing if selecting the same image
                    break;
                } else {

                    // if it is a match
                    if(shuffleList.get(selectedIndex) == shuffleList.get(i)) {

                        matchCount += 1;
                        tvMatches.setText(matchCount + " of 6 Matches");

                        // stop listening for click
                        imgViews.get(selectedIndex).setOnClickListener(null);
                        imgViews.get(i).setOnClickListener(null);

                        selectedIndex = UNSELECTED;

                        if(matchCount == 6) {
                            gameOver();
                        }

                    } else {

                        // if it is not a match, wait for a second and hide images

                        final int index1 = i;
                        final int index2 = selectedIndex;
                        selectedIndex = UNSELECTED;

                        Toast.makeText(this, "Sorry! Wrong Match!",
                                Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                imgViews.get(index1).setImageResource(R.drawable.ic_launcher_background);
                                imgViews.get(index2).setImageResource(R.drawable.ic_launcher_background);
                            }
                        }, 1500);

                    }
                }

                break;
            }

        }

    }

    private void gameOver() {
       stopTimer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Congratulations!  You finished in " + tvTimer.getText() + "!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("mm : ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer() {
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            tvTimer.setText(getDateFromMillis(timeInMilliseconds));
            customHandler.postDelayed(this, 1000);
        }
    };
}
