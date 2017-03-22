package com.parse.starter.RatingActivity;

import com.parse.ParseFile;

/**
 * Created by Colin on 3/21/2017.
 */

public class RatingCatObject {
    private ParseFile catImage;
    private int catTotalRatings, catPositiveRatings;
    private String imageID;
    RatingCatObject(String objID, ParseFile Image, int totalRatings, int positiveRatings)
    {
        catImage = Image;
        catTotalRatings = totalRatings;
        catPositiveRatings = positiveRatings;
        imageID = objID;
    }

    ParseFile getcatImage()
    {
        return this.catImage;
    }
    String getImageID() { return this.imageID; }
}
