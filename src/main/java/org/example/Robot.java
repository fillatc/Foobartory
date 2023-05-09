package org.example;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.example.resources.TaskEnum;
import org.example.resources.Bar;
import org.example.resources.Foo;
import org.example.resources.FooBar;
import org.example.utils.RandomSingleton;
import org.example.utils.Utils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.example.utils.Constants.*;

public class Robot implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);

  private final UUID serialNumber;
  private final SupplyDepot supplyDepot;
  private final RandomSingleton random;
  private TaskEnum task;


  public Robot(SupplyDepot supplyDepot, RandomSingleton random, TaskEnum task) {
    this.serialNumber = UUID.randomUUID();
    this.supplyDepot = supplyDepot;
    this.random = random;
    this.task = task;
    LOGGER.info("New robot, serialNumber: {}", serialNumber);
  }

  @Override
  public void run() {
    startWorking();
  }

  private void startWorking() {
    MDC.put("robot", this.serialNumber.toString());

    while (supplyDepot.isRunning()) {
      TaskEnum newTask;
      if (shouldSwitch()) {
        LOGGER.debug("Force switch!");
        newTask = supplyDepot.getTotalFoo() > supplyDepot.getTotalBar() ? TaskEnum.MINING_BAR : TaskEnum.MINING_FOO;
      } else {
        newTask = searchTask();
      }
      boolean switchingTask = this.task != newTask;
      this.task = newTask;

        MDC.put("task", this.task.name());
        switch (this.task) {
        case MINING_FOO -> mineFoo(switchingTask);
        case MINING_BAR -> mineBar(switchingTask);
        case ASSEMBLING_FOO_BAR -> assembleFooBar(switchingTask);
        case SELLING_FOO_BAR -> sellFooBar(switchingTask);
        case BUYING_ROBOT -> buyRobot(switchingTask);
      }
    }
    MDC.clear();
  }

  private boolean shouldSwitch() {
    return switch (this.task) {
      case ASSEMBLING_FOO_BAR -> supplyDepot.getTotalFoo() == 0 || supplyDepot.getTotalBar() == 0;
      case BUYING_ROBOT -> supplyDepot.getTotalMoney() < ROBOT_PRICE || supplyDepot.getTotalFoo() < FOO_TO_BUILD_ROBOT;
      case SELLING_FOO_BAR -> supplyDepot.getTotalFooBar() < NUMBER_FOO_BAR_TO_SELL;
      default -> false;
    };
  }

  private TaskEnum searchTask() {
    if (supplyDepot.getTotalMoney() >= ROBOT_PRICE && supplyDepot.getTotalFoo() >= FOO_TO_BUILD_ROBOT) {
      return TaskEnum.BUYING_ROBOT;
    } else if (supplyDepot.getTotalFooBar() >= NUMBER_FOO_BAR_TO_SELL) {
      return TaskEnum.SELLING_FOO_BAR;
    } else if (supplyDepot.getTotalFoo() >= 11 || supplyDepot.getTotalBar() >= 5) {
      return TaskEnum.ASSEMBLING_FOO_BAR;
    }
    return this.task;
  }

  public void mineFoo(final boolean switchingTask) {
    var foo = new Foo();
    LOGGER.debug("Mining foo: {}.", foo.getSerialNumber());
    Utils.sleep(MINE_FOO_TIME, switchingTask);
    supplyDepot.addFoo(foo);
  }

  public void mineBar(final boolean switchingTask) {
    var bar = new Bar();
    int result = random.getNextRandom(MINE_BAR_MIN_TIME, MINE_BAR_MAX_TIME);
    LOGGER.debug("Mining bar: {}", bar.getSerialNumber());
    Utils.sleep(result, switchingTask);
    supplyDepot.addBar(bar);
  }

  public void assembleFooBar(final boolean switchingTask) {
      Optional<Pair<Foo, Bar>> opt = supplyDepot.removeFooAndBar();
      if (opt.isPresent()) {
          Foo foo = opt.get().getValue0();
          Bar bar = opt.get().getValue1();
          int result = random.getNextRandom(0, 100);
          Utils.sleep(ASSEMBLE_FOO_BAR_TIME, switchingTask);
          if (result <= 60) {
              var fooBar = new FooBar();
              LOGGER.debug("Assemble fooBar: {} from foo: {} and bar: {}", fooBar.getSerialNumber(), foo.getSerialNumber(), bar.getSerialNumber());
              supplyDepot.addFooBar(fooBar);
          } else {
              LOGGER.debug("Assembling failed! foo: {} | bar: {}", foo.getSerialNumber(), bar.getSerialNumber());
              supplyDepot.addBar(bar);
          }
      } else {
          LOGGER.warn("Can't assemble foobar !");
      }
  }

  public void sellFooBar(final boolean switchingTask) {
    List<FooBar> fooBars = supplyDepot.removeFooBar(NUMBER_FOO_BAR_TO_SELL);

    if (fooBars.isEmpty()) {
      LOGGER.warn("Can't sell foobar, you don't have the 5 fooBar required.");
    } else {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sell fooBar: {}.",
                fooBars.stream()
                    .map(fooBar -> fooBar.getSerialNumber().toString())
                    .collect(Collectors.joining(", "))
            );
        }
      Utils.sleep(SELL_FOO_BAR_TIME, switchingTask);
      supplyDepot.addMoney(5);
    }
  }


  public void buyRobot(final boolean switchingTask) {
      Optional<Pair<List<Foo>, Integer>> opt = supplyDepot.removeFooAndMoney(FOO_TO_BUILD_ROBOT, ROBOT_PRICE);
      if (opt.isPresent()) {
          LOGGER.info("Buying a new robot.");
          Utils.sleep(BUY_ROBOT_TIME, switchingTask);
          supplyDepot.addRobot(TaskEnum.MINING_BAR);
      } else {
          LOGGER.warn("Can't buy robot !");
      }
  }

}
