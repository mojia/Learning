package com.m.server.location.randomPointModel;

import java.util.Random;

public class RandomHelper {
    private static final Random rand = new Random();
    private static final int MAX_RADIUS = 200;

    public static Point randomOnePoint() {
        int x = randomRadius();
        int y = randomRadius();
        int symbolX = randonisPositive() == true ? 1 : (-1);
        int symbolY = randonisPositive() == true ? 1 : (-1);

        return new Point(MAX_RADIUS + x * symbolX, MAX_RADIUS + y * symbolY);
    }

    public static int randomRadius() {
        // return MAX_RADIUS * rand.nextDouble();
        return rand.nextInt(MAX_RADIUS);
    }

    public static boolean randonisPositive() {
        int result = rand.nextInt(2);
        if (result == 0) {
            return false;
        } else {
            return true;
        }
    }
}
