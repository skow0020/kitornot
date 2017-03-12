package com.parse.starter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RatingActivity extends AppCompatActivity {
    int randomInt;
    Random generator = new Random();
    ratingCatObject chosenCat;
    Bitmap chosenCatImage;
    public static List<ratingCatObject> ratingCatObjects = new ArrayList<>();
    int totalRatings, positiveRatings;
    String catID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("images");
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null)
                {
                    if (objects.size() > 0)
                    {
                        for (ParseObject object : objects)
                        {
                            ParseFile imgFile = (ParseFile) object.get("image");
                            totalRatings = (Integer) object.get("totalRatings");
                            positiveRatings = (Integer) object.get("positiveRatings");
                            catID = object.getObjectId();
                            ratingCatObject ratingCat = new ratingCatObject(catID, imgFile, totalRatings, positiveRatings);
                            ratingCatObjects.add(ratingCat);

                            if (ratingCatObjects.size() == objects.size())
                            {
                                Toast.makeText(getApplication().getBaseContext(), "Cats loaded successfully", Toast.LENGTH_LONG).show();
                                chosenCat = loadRandomCat();
                            }
                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplication().getBaseContext(), "Images failed to laod", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cuteBtn(View view)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("images");

        query.getInBackground(chosenCat.getImageID(), new GetCallback<ParseObject>() {
            public void done(ParseObject catObject, ParseException e) {
                if (e == null) {
                    catObject.increment("positiveRatings");
                    catObject.increment("totalRatings");
                    catObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                Log.i("Rating Updated", " Successful");
                                loadRandomCat();
                            }
                            else
                            {
                                Toast.makeText(getApplication().getBaseContext(), "Rating not updated successfully", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                loadRandomCat();
                            }
                        }
                    });
                }
            }
        });
    }
    public void noBtn(View view)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("images");

        query.getInBackground(chosenCat.getImageID(), new GetCallback<ParseObject>() {
            public void done(ParseObject catObject, ParseException e) {
                if (e == null) {
                    catObject.increment("totalRatings");
                    catObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                Log.i("Rating Updated", " Successful");
                                loadRandomCat();
                            }
                            else
                            {
                                Toast.makeText(getApplication().getBaseContext(), "Rating not updated successfully", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                loadRandomCat();
                            }
                        }
                    });
                }
            }
        });

    }

    public ratingCatObject loadRandomCat()
    {
        randomInt = generator.nextInt(ratingCatObjects.size());
        chosenCat = ratingCatObjects.get(randomInt);

        ParseFile imgFile = chosenCat.getcatImage();

        imgFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e == null)
                {
                    chosenCatImage = BitmapFactory.decodeByteArray(data, 0, data.length);

                    if (chosenCatImage.getHeight() > 4160 || chosenCatImage.getWidth() > 4160)
                    {
                        int nh = (int) ( chosenCatImage.getHeight() * (512.0 / chosenCatImage.getWidth()) );
                        chosenCatImage = Bitmap.createScaledBitmap(chosenCatImage, 512, nh, true);
                    }

                    ImageView imageView = (ImageView) findViewById(R.id.catImage);
                    imageView.setImageBitmap(chosenCatImage);
                }
                else
                {
                    Toast.makeText(getApplication().getBaseContext(), "Random cat image failed to load", Toast.LENGTH_LONG).show();
                }
            }
        });
        setContentView(R.layout.activity_rating);
        return chosenCat;
    }
}

class ratingCatObject
{
    private ParseFile catImage;
    private int catTotalRatings, catPositiveRatings;
    private String imageID;
    public ratingCatObject(String objID, ParseFile Image, int totalRatings, int positiveRatings)
    {
        catImage = Image;
        catTotalRatings = totalRatings;
        catPositiveRatings = positiveRatings;
        imageID = objID;
    }

    public ParseFile getcatImage()
    {
        return this.catImage;
    }
    public int getTotalRatings()
    {
        return this.catTotalRatings;
    }
    public int getPositiveRatings()
    {
        return this.catPositiveRatings;
    }
    public String getImageID() { return this.imageID; }
}
