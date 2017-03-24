package com.parse.starter.TopCats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.CatObject;
import com.parse.starter.ImageAdapter;
import com.parse.starter.R;
import com.parse.starter.UserHome.UserCatDetails;

import java.util.ArrayList;
import java.util.List;

public class TopCatsActivity extends AppCompatActivity {

    private GridView topCatsGrid;
    private ArrayList<Bitmap> catImages = new ArrayList<>();
    public static List<CatObject> catObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_cats);

        TextView topCatsLabel = (TextView) findViewById(R.id.topCatsLabel);
        topCatsLabel.setText(String.format("Top Cats"));
        SetCatGrid();

        //Listening for a click on a cat image - Start TopCatDetails activity showing cat image and info on click
        topCatsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(TopCatsActivity.this, TopCatDetails.class);
                i.putExtra("catListPosition", position);
                startActivity(i);
            }
        });
    }

    public void SetCatGrid()
    {
        topCatsGrid = (GridView) findViewById(R.id.topCats);

        ParseQuery<ParseObject> query = new ParseQuery<>("images");
        query.orderByDescending("averageRating");
        query.setLimit(2);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null)
                {
                    final int numObjectsReturned = objects.size();
                    if (numObjectsReturned == 0) Toast.makeText(getApplication().getBaseContext(), "You have no cat images to load", Toast.LENGTH_LONG).show();
                    for (final ParseObject object : objects)
                    {
                        final int totalRatings = (Integer) object.get("totalRatings");
                        final int positiveRatings = (Integer) object.get("positiveRatings");

                        ParseFile imgFile = (ParseFile) object.get("image");

                        imgFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null)
                                {
                                    Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    CatObject cat = new CatObject(img, totalRatings, positiveRatings);
                                    catImages.add(img);
                                    catObjects.add(cat);
                                    if (catObjects.size() == numObjectsReturned) topCatsGrid.setAdapter(new ImageAdapter(getApplicationContext(), catImages));
                                }
                                else Toast.makeText(getApplication().getBaseContext(), "Top Cats images failed to load", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else Toast.makeText(getApplication().getBaseContext(), "Top cats failed to load", Toast.LENGTH_LONG).show();
            }
        });
    }

}
