package com.backgroundback.userinput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/* Tests for the InputParser, focusing on reading user input accurately. */
public class InputParserTest {

   InputParser parser;

   @BeforeEach
   public void setup() {
      parser = new InputParser();
   }

   @Test
   public void testParseAirportIdentifiers_commasNoSpaces() {
      assertEquals(parser.parseAirportIdentifiers("c65,ksbn"), Arrays.asList("c65", "ksbn"));
   }

   @Test
   public void testParseAirportIdentifiers_commasAndSpaces() {
      assertEquals(parser.parseAirportIdentifiers("c65, ksbn"), Arrays.asList("c65", "ksbn"));
   }

   @Test
   public void testParseAirportIdentifiers_commasAndSpacesAdvanced() {
      assertEquals(parser.parseAirportIdentifiers("c65    ,,,,   , ,  , ksbn"), Arrays.asList("c65", "ksbn"));
   }

   @Test
   public void testParseAirportIdentifiers_emptyString() {
      assertEquals(parser.parseAirportIdentifiers(""), Collections.emptyList());
   }
}
