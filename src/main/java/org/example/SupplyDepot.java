package org.example;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.example.resources.TaskEnum;
import org.example.resources.Bar;
import org.example.resources.Foo;
import org.example.resources.FooBar;
import org.example.utils.RandomSingleton;
import org.javatuples.Pair;

import static org.example.utils.Constants.ROBOT_PRICE;

public class SupplyDepot {

  private final AtomicBoolean running = new AtomicBoolean(true);
  private final AtomicInteger totalRobot = new AtomicInteger(0);
  private final AtomicInteger money = new AtomicInteger(0);
  private final LinkedList<Foo> fooList = new LinkedList<>();
  private final LinkedList<Bar> barList = new LinkedList<>();
  private final LinkedList<FooBar> fooBarList = new LinkedList<>();
  private final LinkedList<Robot> robotIdle = new LinkedList<>();


  public void stop() {
    synchronized (this) {
      this.running.getAndSet(false);
    }
  }

  public boolean isRunning() {
    synchronized (this) {
      return this.running.get();
    }
  }

  public void addFoo(final Foo foo) {
    synchronized (this) {
      this.fooList.add(foo);
    }
  }

  public Optional<Pair<Foo, Bar>> removeFooAndBar() {
      synchronized (this) {
          if (fooList.isEmpty() || barList.isEmpty()) {
              return Optional.empty();
          }

          Foo foo = fooList.remove();
          Bar bar = barList.remove();
          return Optional.of(new Pair<>(foo, bar));
      }
  }

   public int getTotalFoo() {
    synchronized (this) {
      return this.fooList.size();
    }
  }

  public void addBar(final Bar bar) {
    synchronized (this) {
      this.barList.add(bar);
    }
  }

  public int getTotalBar() {
    synchronized (barList) {
      return barList.size();
    }
  }

  public void addFooBar(final FooBar fooBar) {
    synchronized (this) {
      this.fooBarList.add(fooBar);
    }
  }

  public List<FooBar> removeFooBar(final int numberOfFooBar) {
    synchronized (this) {
        if (this.fooBarList.size() >= numberOfFooBar) {
            List<FooBar> fooBars = new ArrayList<>();
            IntStream.range(0, numberOfFooBar).forEach(value -> fooBars.add(this.fooBarList.remove(0)));
            return fooBars;
        }
        return Collections.emptyList();
    }
  }

  public Optional<Pair<List<Foo>, Integer>> removeFooAndMoney(final int numberOfFoo, final int price) {
      synchronized (this) {
          if (this.fooList.size() >= numberOfFoo && this.money.get() >= ROBOT_PRICE) {
              List<Foo> foos = new ArrayList<>();
              IntStream.range(0, numberOfFoo).forEach(value -> foos.add(this.fooList.remove(0)));
              Integer cash = money.getAndAdd(price * (-1));
              return Optional.of(new Pair<>(foos, cash));
          }
          return Optional.empty();
      }
  }

  public int getTotalFooBar() {
    synchronized (this) {
      return this.fooBarList.size();
    }
  }

  public int getTotalRobot() {
    synchronized (this) {
      return this.totalRobot.get();
    }
  }

  public void addRobot(final TaskEnum taskEnum) {
      synchronized (this) {
          totalRobot.getAndIncrement();
          robotIdle.add(new Robot(this, RandomSingleton.getInstance(), taskEnum));
      }
  }

  public Optional<Robot> getIdleRobot() {
    synchronized (this) {
      if (robotIdle.isEmpty()) {
          return Optional.empty();
      }
      return Optional.of(this.robotIdle.remove(0));
    }
  }

  public int getTotalMoney() {
    return money.get();
  }

  public void addMoney(final int price) {
    money.getAndAdd(price);
  }

}
