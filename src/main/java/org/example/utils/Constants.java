package org.example.utils;

public class Constants {

  // Time constant in milliseconds
  public static final int MINE_FOO_TIME = 1_000;
  public static final int MINE_BAR_MIN_TIME = 500;
  public static final int MINE_BAR_MAX_TIME = 2_000;
  public static final int ASSEMBLE_FOO_BAR_TIME = 2_000;
  public static final int SELL_FOO_BAR_TIME = 5_000;
  public static final int BUY_ROBOT_TIME = 1_000;
  public static final int SWITCHING_ACTIVITY = 5_000;

  // Number of resources to achieve some task
  public static final int NUMBER_OF_ROBOT_GOAL = 30;
  public static final int ROBOT_PRICE = 3;
  public static final int FOO_TO_BUILD_ROBOT = 6;

  /**
   * Constant use to slow or increase the speed of the game.
   * The higher is the value the slowest the game will be.
   */
  public static final int SPEED = 100;

  private Constants() {
    //
  }
}
