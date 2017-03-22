package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.starter.UserHome.UserHome;
import com.parse.starter.UserHome.CatObject;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;

public class UserCatDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cat_details);

        Intent i = getIntent();
        int catListPosition = i.getIntExtra("catListPosition", 0);

        loadDetails(catListPosition);
    }

    private void loadDetails(int catListPosition)
    {
        CatObject cat = UserHome.catObjects.get(catListPosition);

        ImageView catImage = (ImageView) findViewById(R.id.catImage);
        catImage.setImageBitmap(cat.getCatImage());

        TextView percentText = (TextView) findViewById(R.id.percent);
        DecimalFormat df = new DecimalFormat("#.##");

        if (cat.getTotalRatings() > 0)
        {
            percentText.setText(String.format("%s%%", df.format(cat.getPercentage())));
        }
        else
        {
            percentText.setText(R.string.no_ratings);
        }
    }
}
