package com.parse.starter;

import android.graphics.Bitmap;

public class CatObject {
    private Bitmap catImage;
    private double catTotalRatings, catPositiveRatings;
    private String imageID;

    public CatObject(String objID, Bitmap Image, int totalRatings, int positiveRatings)
    {
        imageID = objID;
        catImage = Image;
        catTotalRatings = totalRatings;
        catPositiveRatings = positiveRatings;
    }

    public double getPercentage()
    {
        if (catTotalRatings == 0)  return 0;
        else return 100*catPositiveRatings/catTotalRatings;
    }

    public double getTotalRatings() { return catTotalRatings; }

    public Bitmap getCatImage() {return catImage; }
}
