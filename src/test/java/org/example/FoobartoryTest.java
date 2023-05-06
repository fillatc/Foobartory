package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.example.domain.ActivityEnum;
import org.example.domain.Bar;
import org.example.domain.Foo;
import org.example.domain.FooBar;
import org.example.utils.RandomSingleton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FoobartoryTest {

  private final RandomSingleton random = RandomSingleton.getInstance();

  @Test
  void mineFooTest() {
    //GIVEN
    var supplyDepot = new SupplyDepot();
    var robot = new Robot(supplyDepot, random, ActivityEnum.MINING_FOO);

    //WHEN
    robot.mineFoo(false);

    //THEN
    assertEquals(1, supplyDepot.getTotalFoo());
  }

  @Test
  void mineBarTest() {
    //GIVEN
    var supplyDepot = new SupplyDepot();
    var robot = new Robot(supplyDepot, random, ActivityEnum.MINING_BAR);

    //WHEN
    robot.mineBar(false);

    //THEN
    assertEquals(1, supplyDepot.getTotalBar());
  }

  @Test
  void assembleFooBarTest() {
    // GIVEN
    var supplyDepot = new SupplyDepot();
    supplyDepot.addBar(new Bar());
    supplyDepot.addFoo(new Foo());
    RandomSingleton random = Mockito.mock(RandomSingleton.class);
    when(random.getNextRandom(0, 100)).thenReturn(1);

    var robot = new Robot(supplyDepot, random, ActivityEnum.ASSEMBLING_FOO_BAR);

    // WHEN
    robot.assembleFooBar(false);

    // THEN
    assertEquals(0, supplyDepot.getTotalBar());
    assertEquals(0, supplyDepot.getTotalFoo());
    assertEquals(1, supplyDepot.getTotalFooBar());
  }

  @Test
  void assembleFooBarTest_with_failed_assembling() {
    // GIVEN
    var supplyDepot = new SupplyDepot();
    supplyDepot.addBar(new Bar());
    supplyDepot.addFoo(new Foo());
    RandomSingleton random = Mockito.mock(RandomSingleton.class);
    when(random.getNextRandom(0, 100)).thenReturn(70);

    var robot = new Robot(supplyDepot, random, ActivityEnum.ASSEMBLING_FOO_BAR);

    // WHEN
    robot.assembleFooBar(false);

    // THEN
    assertEquals(1, supplyDepot.getTotalBar());
    assertEquals(0, supplyDepot.getTotalFoo());
    assertEquals(0, supplyDepot.getTotalFooBar());
  }

  @Test
  void sellFooBarTest() {
    // GIVEN
    var supplyDepot = new SupplyDepot();
    for (var counter = 0; counter < 5; counter ++) {
      supplyDepot.addFooBar(new FooBar());
    }
    assertEquals(5, supplyDepot.getTotalFooBar());
    var robot = new Robot(supplyDepot, random, ActivityEnum.SELLING_FOO_BAR);

    // WHEN
    robot.sellFooBar(false);

    // THEN
    assertEquals(0, supplyDepot.getTotalFooBar());
    assertEquals(5, supplyDepot.getTotalMoney());
  }

  @Test
  void buyRobotTest() {
    // GIVEN
    var supplyDepot = new SupplyDepot();
    for (var counter = 0; counter < 6; counter ++) {
      supplyDepot.addFoo(new Foo());
    }
    assertEquals(6, supplyDepot.getTotalFoo());
    supplyDepot.addMoney(3);
    var robot = new Robot(supplyDepot, random, ActivityEnum.BUYING_ROBOT);

     // WHEN
     robot.buyRobot(false);

     // THEN
     assertEquals(0, supplyDepot.getTotalFoo());
     assertEquals(0, supplyDepot.getTotalMoney());
     assertEquals(1, supplyDepot.getTotalRobot());
     assertTrue(supplyDepot.getIdleRobot().isPresent());
  }

}
