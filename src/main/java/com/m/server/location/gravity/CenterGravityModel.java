package com.m.server.location.gravity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

/**
 * 
 * 针对wifi定位设计的重心模型，发现根本不收敛。此方法不可取，需要重新找收敛的模型 
 * 
 * @author wangxin09
 * @created 2013-1-26
 * 
 * @version 1.0
 */
public class CenterGravityModel {

    private static final Random rand = new Random();
    private CenterGravityService service = new CenterGravityService();
    private static final int MAX_RADIUS = 200;

    private static final List<Point> basePoints = new ArrayList<Point>();
    private PointService servie = new PointService();

    public static void main(String[] args) {
        CenterGravityModel model = new CenterGravityModel();
        model.work();
    }

    public void work() {
        Point target = null;
        for (int i = 0; i < 100; i++) {
            Point onePoint = randomOnePoint();
            basePoints.add(onePoint);
            target = servie.center(basePoints);
            if (i % 10 == 0) {
                System.out.println(distanceFromCenter(target) + "\t\t\t"
                        + target);
            }
        }
    }

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

    public int distanceFromCenter(Point point) {
        return (int) ((point.getX() - MAX_RADIUS) * (point.getX() - MAX_RADIUS) + (point
                .getY() - MAX_RADIUS) * (point.getY() - MAX_RADIUS));
    }

    @Test
    public void testcase() {
        System.out.println(randomRadius());
    }

}
