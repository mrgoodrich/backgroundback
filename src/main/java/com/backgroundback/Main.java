package com.backgroundback;

import com.backgroundback.model.Airport;
import com.backgroundback.model.AirportSummary;
import com.backgroundback.model.WeatherConditions;
import com.backgroundback.networking.AirportLoader;
import com.backgroundback.networking.FFOkHttpClientBuilder;
import com.backgroundback.networking.WeatherConditionsLoader;
import com.backgroundback.runner.AirportSummaryRunner;
import com.backgroundback.transformers.AirportSummaryTransformer;
import com.backgroundback.transformers.ResponseTransformer;
import com.backgroundback.userinput.InputParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import okhttp3.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {

   public static void main(String[] args) {
      printIntroAndInstructions();

      AirportSummaryRunner.runAirportSummaryTool();
   }

   private static void printIntroAndInstructions() {
      System.out.println("********************************************************");
      System.out.println("*  Airport Summary Tool     by Matt Goodrich           *");
      System.out.println("*                                                      *");
      System.out.println("*  Usage: Enter one or more ICAO airport identifiers.  *");
      System.out.println("*                                                      *");
      System.out.println("*  For example: kpwm, kaus                             *");
      System.out.println("*  You can enter more identifiers at any time.         *");
      System.out.println("********************************************************\n");
   }
}
