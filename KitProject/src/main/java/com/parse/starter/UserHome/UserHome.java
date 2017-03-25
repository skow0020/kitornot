package com.parse.starter.UserHome;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import java.io.InputStream;
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
                    catObjects.clear();
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
                                    CatObject cat = new CatObject(object.getObjectId(), img, totalRatings, positiveRatings);

                                    catImages.add(img);
                                    catObjects.add(cat);
                                    if (catObjects.size() == numObjectsReturned)
                                    {
                                        userCatGrid.setAdapter(new ImageAdapter(getApplicationContext(), catImages));
                                    }
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

    public static int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 1024 || rotatedHeight > 1024) {
            float widthRatio = ((float) rotatedWidth) / ((float) 1024);
            float heightRatio = ((float) rotatedHeight) / ((float) 1024);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else srcBitmap = BitmapFactory.decodeStream(is);
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

    //Adding Cat images from internal Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            Bitmap bitmapImage;
            try {
                bitmapImage = getCorrectlyOrientedImage(this, selectedImage);

                //Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

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

