package com.skow.kitornot2.UserHome;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.skow.kitornot2.CatObject;
import com.skow.kitornot2.R;

import java.text.DecimalFormat;

public class UserCatDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cat_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent i = getIntent();
        int catListPosition = i.getIntExtra("catListPosition", 0);

        loadDetails(catListPosition);
    }

    private void loadDetails(int catListPosition)
    {
        ImageView catImage = (ImageView) findViewById(R.id.catImage);
        TextView percentText = (TextView) findViewById(R.id.percent);
        CatObject cat = UserHomeActivity.catObjects.get(catListPosition);

        TextView cuteness = (TextView) findViewById(R.id.cuteness);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/vollkorn-reg.ttf");
        cuteness.setTypeface(custom_font);

        catImage.setImageBitmap(cat.getCatImage());

        DecimalFormat df = new DecimalFormat("#.##");

        if (cat.getTotalRatings() > 0) percentText.setText(String.format("%s%%", df.format(cat.getPercentage())));
        else percentText.setText(R.string.no_ratings);
    }
}
