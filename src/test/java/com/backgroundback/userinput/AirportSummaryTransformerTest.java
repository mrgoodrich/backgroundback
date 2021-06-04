package com.backgroundback.userinput;

import com.backgroundback.model.WeatherConditions.Report.Conditions;
import com.backgroundback.model.WeatherConditions.Report.Conditions.CloudLayers;
import com.backgroundback.transformers.AirportSummaryTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/** Tests the AirportSummaryTransformer, which contains some complex transformations especially worth testing. */
public class AirportSummaryTransformerTest {

   AirportSummaryTransformer transformer;

   @BeforeEach
   public void setup() {
      transformer = new AirportSummaryTransformer(new ObjectMapper());
   }

   @Test
   public void testCelsiusToFahrenheit_freezing() {
      assertEquals(transformer.celsiusToFahrenheit(0), 32);
   }

   @Test
   public void testCelsiusToFahrenheit_warm() {
      assertEquals(transformer.celsiusToFahrenheit(20), 68);
   }

   @Test
   public void testCelsiusToFahrenheit_negative() {
      assertEquals(transformer.celsiusToFahrenheit(-20), -4);
   }

   @Test
   public void testCelsiusToFahrenheit_reallyHot() {
      assertEquals(transformer.celsiusToFahrenheit(100), 212);
   }

   @Test
   public void testGetGreatestCloudCoverage_vanilla() {
      CloudLayers[] testLayers = new CloudLayers[]{ new CloudLayers("bkn", 1000, true) };
      assertEquals(transformer.getGreatestCloudCoverage(testLayers), "Broken at 1000ft");
   }

   @Test
   public void testGetGreatestCloudCoverage_clear() {
      CloudLayers[] testLayers = new CloudLayers[]{ new CloudLayers("clr", 1000, true) };
      assertEquals(transformer.getGreatestCloudCoverage(testLayers), "Clear");
   }

   @Test
   public void testGetGreatestCloudCoverage_prioritizesMoreObscuration() {
      CloudLayers[] testLayers = new CloudLayers[]{
            new CloudLayers("bkn", 1000, true),
            new CloudLayers("sct", 1000, true)
      };
      assertEquals(transformer.getGreatestCloudCoverage(testLayers), "Broken at 1000ft");
   }

   @Test
   public void testGetGreatestCloudCoverage_prioritizesLowerAltitudeAsSecondarySort() {
      CloudLayers[] testLayers = new CloudLayers[]{
            new CloudLayers("bkn", 1000, true),
            new CloudLayers("bkn", 2500, true)
      };
      assertEquals(transformer.getGreatestCloudCoverage(testLayers), "Broken at 1000ft");
   }

   @Test
   public void testGetGreatestCloudCoverage_lotsOfLayers() {
      CloudLayers[] testLayers = new CloudLayers[]{
            new CloudLayers("bkn", 1000, true),
            new CloudLayers("bkn", 1000, true),
            new CloudLayers("sct", 1000, true),
            new CloudLayers("bkn", 1000, true),
            new CloudLayers("bkn", 1000, true),
            new CloudLayers("ovc", 20000, true),
            new CloudLayers("bkn", 1000, true),
            new CloudLayers("sct", 2500, true)
      };
      assertEquals(transformer.getGreatestCloudCoverage(testLayers), "Overcast at 20000ft");
   }

   @Test
   public void testGetGreatestCloudCoverage_noLayers() {
      CloudLayers[] testLayers = new CloudLayers[]{ };
      assertEquals(transformer.getGreatestCloudCoverage(testLayers), "Skies clear");
   }

   @Test
   public void testKnotsToMph_various() {
      assertEquals(transformer.knotsToMph(100), 115);
      assertEquals(transformer.knotsToMph(0), 0);
      assertEquals(transformer.knotsToMph(247), 284);
   }

   @Test
   public void testGetSecondaryIntercardinalWindDirection_variousDirections() {
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(15), "NNE");
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(45), "NE");
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(87), "E");
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(144), "SE");
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(178), "S");
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(210), "SSW");
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(253), "WSW");
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(316), "NW");
   }

   @Test
   public void testGetSecondaryIntercardinalWindDirection_northEdgeCase() {
      assertEquals(transformer.getSecondaryIntercardinalWindDirection(360), "N");
   }

   @Test
   public void testGetTimeOffset() {
      String actualOffset =
            transformer.getTimeOffset(
                  new Conditions("2021-06-04T23:40:00+0000"), "2021-06-03T17:20:00+0000");
      assertEquals(actualOffset, "30:20");
   }
}
