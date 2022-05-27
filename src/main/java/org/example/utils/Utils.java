package org.example.utils;

import java.util.Random;

public class Utils {

  public static int getNextRandom(int min, int max) {
    var random = new Random();
    return  random.nextInt(max - min) + min;
  }

}
