package org.example.utils;

import static org.example.utils.Constants.SPEED;
import static org.example.utils.Constants.SWITCHING_ACTIVITY;

import java.util.Random;

public class Utils {

  public static int getNextRandom(int min, int max) {
    var random = new Random();
    return  random.nextInt(max - min) + min;
  }

  public static void sleep(int duration, boolean switchingActivity) {
    try {
      long interval = switchingActivity ?  duration + SWITCHING_ACTIVITY : duration;
      Thread.sleep(interval / SPEED);
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }

  public static void join(Thread thread) {
    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }

  private Utils() {
    //
  }
}
