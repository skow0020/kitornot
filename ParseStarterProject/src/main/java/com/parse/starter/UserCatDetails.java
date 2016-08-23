package com.parse.starter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;


import java.io.FileNotFoundException;

public class UserCatDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cat_details2);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(UserCatDetails.this.openFileInput("catImage"));
            ImageView catImage = (ImageView) findViewById(R.id.catImage);
            catImage.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
