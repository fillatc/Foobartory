package org.example;

import java.util.ArrayList;
import java.util.List;

import org.example.domain.ActivityEnum;
import org.example.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Foobartory {

  private static final Logger LOGGER = LoggerFactory.getLogger(Foobartory.class);

  public static void main(String[] args) {
    LOGGER.info("Starting the game!");

    List<Thread> threads = new ArrayList<>();

    var supplyDepot = new SupplyDepot();
    supplyDepot.addRobot(ActivityEnum.MINING_FOO);
    supplyDepot.addRobot(ActivityEnum.MINING_BAR);
   
    long start = System.currentTimeMillis();

    while (supplyDepot.getTotalRobot() < 30) {
      var robot = supplyDepot.removeRobot();
      if (robot != null) {
        var  thread = new Thread(robot);
        thread.start();
        threads.add(thread);

        LOGGER.info("Total robot: {}.", supplyDepot.getTotalRobot());
      }
    }
    supplyDepot.stop();

    long stop = System.currentTimeMillis();
    LOGGER.debug("Waiting for the thread to stop...");
    threads.forEach(Utils::join);

    LOGGER.info("You have reach {} robot !", supplyDepot.getTotalRobot());
    LOGGER.info("Your game time: {} ms.", stop - start);
    LOGGER.info("End of the game, bye.");
  }
}
