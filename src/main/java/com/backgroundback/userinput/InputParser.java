package com.backgroundback.userinput;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputParser {

   public InputParser() {}

   /**
    * A simple function for parsing airport identifiers.
    *
    * @param rawInput the raw user input typed from the CLI.
    * @return a list of airport identifiers to search.
    */
   public List<String> parseAirportIdentifiers(String rawInput) {
      List<String> ids =
            Arrays.stream(rawInput.replaceAll(" ", ",").split(","))
                  .filter(v -> !v.isEmpty())
                  .map(String::toLowerCase)
                  .collect(Collectors.toList());
      if (ids.isEmpty()) {
         System.out.println("No valid ids entered. Please try again.");
      }
      return ids;
   }
}
