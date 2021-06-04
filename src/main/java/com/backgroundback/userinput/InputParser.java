package com.backgroundback.userinput;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputParser {

   public InputParser() { }

   public List<String> parseAirportIdentifiers(String rawInput) {
      List<String> ids =
            Arrays.stream(rawInput.replaceAll(" ", ",").split(","))
                  .filter(v -> !v.isEmpty())
            .collect(Collectors.toList());
      if (ids.isEmpty()) {
         System.out.println("No valid ids entered. Please try again.");
      }
      return ids;
   }
}
