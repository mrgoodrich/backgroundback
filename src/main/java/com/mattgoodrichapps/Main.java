package com.mattgoodrichapps;

import java.util.Scanner;

public class Main {

   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);

      System.out.println("Please enter one or more airport identifiers:");
      while (scanner.hasNextLine()) {
         String airportIdentifiersRaw = scanner.nextLine();

         System.out.println("Entered " + airportIdentifiersRaw);
      }
   }
}
