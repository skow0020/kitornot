package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.FileNotFoundException;

public class UserCatDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cat_details2);

        Intent i = getIntent();
        int catListPosition = i.getIntExtra("catListPosition", 0);

        loadCatImage();
        loadDetails(catListPosition);

    }

    //Get cat image from local storage and load in activity
    private void loadCatImage()
    {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(UserCatDetails.this.openFileInput("catImage"));
            ImageView catImage = (ImageView) findViewById(R.id.catImage);
            catImage.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDetails(int catListPosition)
    {
        TextView percentText = (TextView) findViewById(R.id.percent);
        String percent;
        catObject cat = (catObject) UserHome.catObjects.get(catListPosition);

        double catTotalRatings = (double)cat.getTotalRatings();
        double catPositiveRatings = (double)cat.getPositiveRatings();
        if (catTotalRatings > 0)
        {
            percent = Double.toString(100*catPositiveRatings/catTotalRatings);
            percentText.setText(percent + "%");
        }
        else
        {
            percentText.setText("No ratings yet");
        }





    }

}
