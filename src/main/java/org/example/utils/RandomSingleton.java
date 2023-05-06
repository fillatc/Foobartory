package org.example.utils;

import java.util.Random;

public class RandomSingleton {

    private static RandomSingleton instance = null;

    private final Random random;

    public static RandomSingleton getInstance() {
        if (instance == null) {
            instance = new RandomSingleton();
        }
        return instance;
    }

    public int getNextRandom(int min, int max) {
        return this.random.nextInt(max - min) + min;
    }

    private RandomSingleton() {
        this.random = new Random();
    }
}
