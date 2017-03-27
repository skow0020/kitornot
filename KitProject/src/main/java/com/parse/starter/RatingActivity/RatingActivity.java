package com.parse.starter.RatingActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
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
import com.parse.starter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RatingActivity extends AppCompatActivity
{
    private ImageButton thumbUp, thumbDown;
    private int totalRatings, positiveRatings;
    private Random generator = new Random();
    private RatingCatObject chosenCat;
    private Bitmap chosenCatImage;
    private List<RatingCatObject> ratingCatObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        thumbUp = (ImageButton) findViewById(R.id.cuteBtn);
        thumbDown = (ImageButton) findViewById(R.id.notBtn);
        thumbDown.setClickable(false);
        thumbUp.setClickable(false);
        ratingCatObjects = new ArrayList<>();

        ParseQuery<ParseObject> query = new ParseQuery<>("images");
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null)
                {
                    ratingCatObjects.clear();
                    for (ParseObject object : objects)
                    {
                        ParseFile imgFile = (ParseFile) object.get("image");
                        totalRatings = (Integer) object.get("totalRatings");
                        positiveRatings = (Integer) object.get("positiveRatings");
                        RatingCatObject ratingCat = new RatingCatObject(object.getObjectId(), imgFile, totalRatings, positiveRatings);
                        ratingCatObjects.add(ratingCat);

                        if (ratingCatObjects.size() == objects.size())
                        {
                            Toast.makeText(getApplication().getBaseContext(), "Cats loaded successfully", Toast.LENGTH_LONG).show();
                            chosenCat = loadRandomCat();
                        }
                    }
                }
                else Toast.makeText(getApplication().getBaseContext(), "Images failed to laod", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cuteBtn(View view)
    {
        thumbUp.setClickable(false);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("images");

        query.getInBackground(chosenCat.getImageID(), new GetCallback<ParseObject>() {
            public void done(ParseObject catObject, ParseException e) {
                if (e == null) {
                    catObject.increment("positiveRatings");
                    catObject.increment("totalRatings");
                    catObject.put("averageRating", Double.parseDouble(catObject.get("positiveRatings").toString())/Double.parseDouble(catObject.get("totalRatings").toString()));
                    catObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) Toast.makeText(getApplication().getBaseContext(), "Rating updated successfully", Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getApplication().getBaseContext(), "Rating failed", Toast.LENGTH_LONG).show();
                            loadRandomCat();
                        }
                    });
                }
            }
        });
    }
    public void noBtn(View view)
    {
        thumbDown.setClickable(false);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("images");

        query.getInBackground(chosenCat.getImageID(), new GetCallback<ParseObject>() {
            public void done(ParseObject catObject, ParseException e) {
                if (e == null) {
                    catObject.increment("totalRatings");
                    catObject.put("averageRating", Double.parseDouble(catObject.get("positiveRatings").toString())/Double.parseDouble(catObject.get("totalRatings").toString()));
                    catObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) Toast.makeText(getApplication().getBaseContext(), "Rating updated successfully!", Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getApplication().getBaseContext(), "Rating failed", Toast.LENGTH_LONG).show();
                            loadRandomCat();
                        }
                    });
                }
            }
        });
    }

    public RatingCatObject loadRandomCat()
    {
        chosenCat = ratingCatObjects.get(generator.nextInt(ratingCatObjects.size()));
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

                    ImageView catImage = (ImageView) findViewById(R.id.catImage);
                    catImage.setImageBitmap(chosenCatImage);
                    thumbDown.setClickable(true);
                    thumbUp.setClickable(true);
                }
                else Toast.makeText(getApplication().getBaseContext(), "Random cat image failed to load", Toast.LENGTH_LONG).show();
            }
        });
        setContentView(R.layout.activity_rating);
        return chosenCat;
    }
}
