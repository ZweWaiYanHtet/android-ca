package com.san.ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
implements View.OnClickListener {

    HashMap<ImageView,Integer> gameImages=new HashMap<ImageView, Integer>();
    List<Integer> selectedList=new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i=0;i<10;i++)
        {
            int id=getResources().getIdentifier("iV"+(i+1),"id",getPackageName());
            ImageView iv = (ImageView) findViewById(id);
            iv.setOnClickListener(this);

            gameImages.put(iv,i+1);
            Log.i("pp",id+" ");
        }
    }

    @Override
    public void onClick(View view){

        for(ImageView iv: gameImages.keySet()){

            if(iv.getId()==view.getId() && !selectedList.contains(gameImages.get(iv)))
            {
                iv.setBackgroundColor(Color.parseColor("#45d4f4"));
                iv.setPadding(2,2,2,2);
                selectedList.add(gameImages.get(iv));

                Log.i("pp",gameImages.get(iv).toString());
            }
        }
        if(selectedList.size() >= 6){
            Intent i = new Intent(this, Game.class);
            startActivity(i);
//            selectedList.clear();
        }
    }

}
