package org.example.utils;

import static org.example.utils.Constants.SPEED;
import static org.example.utils.Constants.SWITCHING_ACTIVITY;

public class Utils {

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
