package com.mattgoodrichapps;

import com.mattgoodrichapps.userinput.InputParser;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      InputParser parser = new InputParser();

      System.out.println("Please enter one or more airport identifiers:");
      while (scanner.hasNextLine()) {
         String airportIdentifiersRaw = scanner.nextLine();

         System.out.println("Entered " + airportIdentifiersRaw);
         parser.parseAirportIdentifiers(airportIdentifiersRaw).stream().forEach(id -> {
            System.out.println("Would find weather for " + id);
         });
      }
   }
}
