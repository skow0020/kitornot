package com.parse.starter.UserHome;

import android.graphics.Bitmap;

import com.parse.ParseFile;

public class CatObject {
    private Bitmap catImage;
    private double catTotalRatings, catPositiveRatings;

    CatObject(Bitmap Image, int totalRatings, int positiveRatings)
    {
        catImage = Image;
        catTotalRatings = totalRatings;
        catPositiveRatings = positiveRatings;
    }

    public double getPercentage()
    {
        if (catTotalRatings == 0) { return 0; }
        else { return 100*catPositiveRatings/catTotalRatings; }
    }

    public double getTotalRatings()
    {
        return this.catTotalRatings;
    }

    public Bitmap getCatImage() {return catImage; }
}
