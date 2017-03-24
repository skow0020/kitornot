package com.parse.starter.UserHome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.CatObject;
import com.parse.starter.ImageAdapter;
import com.parse.starter.R;
import com.parse.starter.RatingActivity.RatingActivity;
import com.parse.starter.TopCats.TopCatsActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserHome extends AppCompatActivity {
    private GridView userCatGrid;
    private ArrayList<Bitmap> catImages = new ArrayList<>();
    public static List<CatObject> catObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        TextView userPage = (TextView) findViewById(R.id.userPage);
        userPage.setText(String.format("%s's cats", ParseUser.getCurrentUser().getUsername()));
        SetCatGrid();

        //Listening for a click on a cat image - Start UserCatDetails activity showing cat image and info on click
        userCatGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(UserHome.this, UserCatDetails.class);
                i.putExtra("catListPosition", position);
                startActivity(i);
            }
        });
    }

    //Query Parse server for a list of users cats and set the Grid
    public void SetCatGrid()
    {
        userCatGrid = (GridView) findViewById(R.id.userCats);

        ParseQuery<ParseObject> query = new ParseQuery<>("images");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername()).orderByDescending("createdAt");

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
                                    if (catObjects.size() == numObjectsReturned) userCatGrid.setAdapter(new ImageAdapter(getApplicationContext(), catImages));
                                }
                                else Toast.makeText(getApplication().getBaseContext(), "Your images failed to load", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else Toast.makeText(getApplication().getBaseContext(), "Your cats failed to load", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
        return true;
    }

    //Open internal images when + button pressed
    public void addCat(View view)
    {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    public void ratingActivity(View view)
    {
        Intent i = new Intent(UserHome.this, RatingActivity.class);
        startActivity(i);
    }

    public void topCatsActivity(View view)
    {
        Intent i = new Intent(UserHome.this, TopCatsActivity.class);
        startActivity(i);
    }

    //Resizing image if it is too large for Parse
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    //Adding Cat images from internal Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();

            try
            {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                if (byteArray.length > 10485760) {
                    int newWidth = (int)Math.round(bitmapImage.getWidth()*0.95);
                    int newHeight = (int)Math.round(bitmapImage.getHeight()*0.95);
                    while (byteArray.length > 10485760)
                    {
                        stream.reset();
                        bitmapImage = getResizedBitmap(bitmapImage, newWidth, newHeight);
                        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArray = stream.toByteArray();
                        newWidth = (int)Math.round(newWidth*0.5);
                        newHeight = (int)Math.round(newHeight*0.5);
                    }
                }

                ParseFile file = new ParseFile("image.png", byteArray);
                ParseObject object = new ParseObject("images");

                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("totalRatings", 0);
                object.put("positiveRatings", 0);
                object.put("averageRating", 0);
                object.put("image", file);

                ParseACL parseACL = new ParseACL();
                parseACL.setPublicReadAccess(true);
                parseACL.setPublicWriteAccess(true);

                object.setACL(parseACL);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                        {
                            Toast.makeText(getApplication().getBaseContext(), "Your image has been posted!", Toast.LENGTH_LONG).show();
                            catObjects.clear();
                            catImages.clear();
                            SetCatGrid();
                        }
                        else Toast.makeText(getApplication().getBaseContext(), "There was an error uploading image - please try again", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (IOException e) {
                Log.i("ImageUpload", " FAILED WEIRDLY!!");
                Toast.makeText(getApplication().getBaseContext(), "There was an error - please try again", Toast.LENGTH_LONG).show();
            }
        }
    }
}

