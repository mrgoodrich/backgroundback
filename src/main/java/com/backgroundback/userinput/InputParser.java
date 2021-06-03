package com.backgroundback.userinput;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputParser {

   public InputParser() { }

   public List<String> parseAirportIdentifiers(String rawInput) {
      return Arrays.stream(rawInput.split(","))
            .map(match -> match.replace(" ", ""))
            .collect(Collectors.toList());
   }
}
