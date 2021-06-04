package com.backgroundback;

import com.backgroundback.runner.AirportSummaryRunner;

public class Main {

   public static void main(String[] args) {
      // Create an instance of the runner. Exits static scoping and improves design.
      new AirportSummaryRunner();
   }
}
