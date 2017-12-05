package com.skow.kitornot2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void createCatObject() throws Exception {
        CatObject cat = new CatObject("12345");
        assertEquals("12345", cat.getImageID());
    }

    @Test
    public void catObjectGetPercentage() throws Exception {
        CatObject cat = new CatObject("12345");
        cat.setCatPositiveRatings(200);
        cat.setTotalRatings(400);
        assertTrue(cat.getTotalRatings() == 400.0);
        double x = cat.getPercentage();
        assertTrue(x == 50.0);
    }

    @Test
    public void catObjectGetPercentageDiv0() throws Exception {
        CatObject cat = new CatObject("12345");
        cat.setCatPositiveRatings(200);
        cat.setTotalRatings(0);
        assertTrue(cat.getTotalRatings() == 0.0);
        double x = cat.getPercentage();
        assertTrue(x == 0.0);
    }

}