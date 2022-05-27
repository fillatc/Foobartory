package org.example;

import java.util.ArrayList;
import java.util.List;

import org.example.domain.ActivityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class Foobartory {

  private static final Logger LOGGER = LoggerFactory.getLogger(Foobartory.class);


  public static void main(String[] args) {
    LOGGER.info("Hello world !");

    List<Thread> threads = new ArrayList<>();

    var supplyDepot = new SupplyDepot();
    var  thread1 = new Thread(new Robot(supplyDepot, ActivityEnum.MINING_FOO));
    var  thread2 = new Thread(new Robot(supplyDepot, ActivityEnum.MINING_BAR));
    
    thread1.start();
    thread2.start();
    threads.add(thread1);
    threads.add(thread2);
    
    long start = System.currentTimeMillis();

    while (supplyDepot.getTotalFooBar() < 2) {
      //Game loop
    }
    supplyDepot.stop();

    long stop = System.currentTimeMillis();

    LOGGER.info("{} ms.", stop - start);
  }
}
