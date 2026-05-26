package com.example.flagquiz;

import org.junit.Test;

import static org.junit.Assert.*;

public class FlagQuizUnitTest {

    @Test
    public void testAddition() {
        int result = 2 + 2;
        assertEquals(4, result);
    }

    @Test
    public void testStringNotEmpty() {
        String testString = "Egypt";
        assertFalse(testString.isEmpty());
    }

    @Test
    public void testCountryNameExtraction() {
        String flagName = "Africa-egypt";
        String countryName = flagName.substring(flagName.indexOf('-') + 1);
        assertEquals("egypt", countryName);
    }

    @Test
    public void testCountryNameWithUnderscore() {
        String flagName = "North_America-usa";
        String countryName = flagName.substring(flagName.indexOf('-') + 1);
        assertEquals("usa", countryName);
    }

    @Test
    public void testScoreCalculation() {
        int correctAnswers = 7;
        int totalQuestions = 10;
        double percent = (double) correctAnswers / totalQuestions * 100;
        assertEquals(70.0, percent, 0.01);
    }
}