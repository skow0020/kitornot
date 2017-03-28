package com.skow.kitornot;

import android.graphics.Bitmap;

import com.parse.ParseFile;

public class CatObject {
    private Bitmap catImage;
    private ParseFile parseImage;
    private int catTotalRatings, catPositiveRatings;
    private String imageID;

    public CatObject(String objID)
    {
        imageID = objID;
    }

    public double getPercentage()
    {
        if (catTotalRatings == 0)  return 0;
        else return 100*(double)catPositiveRatings/catTotalRatings;
    }

    public double getTotalRatings() { return catTotalRatings; }

    public Bitmap getCatImage() {return catImage; }

    public String getImageID() { return imageID; }

    public ParseFile getParseImage() { return parseImage; }

    public void setTotalRatings(int totalRatings) { catTotalRatings = totalRatings; }

    public void setCatPositiveRatings(int totalRatings) { catPositiveRatings = totalRatings; }

    public void setCatImage(Bitmap img) {catImage = img; }

    public void setParseImage(ParseFile file) { parseImage = file; }
}
