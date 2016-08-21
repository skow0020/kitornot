package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserHome extends AppCompatActivity {
    TextView userPage;
    GridView userCatGrid;
    ArrayList<Bitmap> catImages = new ArrayList<Bitmap>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        userPage = (TextView) findViewById(R.id.userPage);
        userPage.setText(ParseUser.getCurrentUser().getUsername() + "'s cats");
        SetCatGrid();

        userCatGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            ImageView catImage = (ImageView) findViewById(R.id.catImage);
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createImageFromBitmap(catImages.get(position));

                Intent i = new Intent(UserHome.this, UserCatDetails.class);
                startActivity(i);
            }
        });
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "catImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public void SetCatGrid()
    {
        userCatGrid = (GridView) findViewById(R.id.userCats);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("images");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null)
                {
                    if (objects.size() > 0)
                    {
                        for (ParseObject object : objects)
                        {
                            final int numObjects = objects.size();
                            ParseFile imgFile = (ParseFile) object.get("image");
                            imgFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null)
                                    {
                                        Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        catImages.add(img);
                                        if (catImages.size() == numObjects)
                                        {
                                            userCatGrid.setAdapter(new ImageAdapter(getApplicationContext(), catImages));
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplication().getBaseContext(), "Your images failed to laod", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplication().getBaseContext(), "Your images failed to laod", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
        return true;
    }

    public void addCat(View view)
    {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    //Resizing image if it is too large for Parse----DOES NOT APPEAR NECESSARY AFTER ALL
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();

            try {

                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("AppInfo", "Image Received");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

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

                object.put("image", file);

                ParseACL parseACL = new ParseACL();
                parseACL.setPublicReadAccess(true);
                object.setACL(parseACL);

                object.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            Log.i("ImageUpload", " Successful");

                            Toast.makeText(getApplication().getBaseContext(), "Your image has been posted!", Toast.LENGTH_LONG).show();

                            catImages.clear();
                            SetCatGrid();
                        } else {
                            Log.i("ImageUpload", " FAILED!!");
                            Toast.makeText(getApplication().getBaseContext(), "There was an error - please try again", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("ImageUpload", " FAILED WEIRDLY!!");
                Toast.makeText(getApplication().getBaseContext(), "There was an error - please try again", Toast.LENGTH_LONG).show();
            }
        }

    }
}

