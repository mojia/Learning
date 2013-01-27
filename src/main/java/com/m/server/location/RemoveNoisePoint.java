package com.m.server.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.m.server.location.gravity.Point;

/**
 * 
 * 
 * @author wangxin09
 * @created 2013-1-19
 * 
 * @version 1.0
 */
public class RemoveNoisePoint {
    private static final double FACTOR = 1.4;

    @Test
    public void testremoveNoisePoint() {
        List<Point> points = new ArrayList<Point>();
        points.add(new Point(0, 1));
        points.add(new Point(0, 0));
        points.add(new Point(1, 1));
        points.add(new Point(1, 0));
        points.add(new Point(0, 0));
        points.add(new Point(2, 1));
        points.add(new Point(3, 0));
        points.add(new Point(2, 3));
        points.add(new Point(2, 4));
        points.add(new Point(3, 7));
        points.add(new Point(2, 5));

        List<Point> result = removeNoisePoint(points);

        for (Point p : result) {
            System.out.println(p);
        }
    }

    public List<Point> removeNoisePoint(List<Point> points) {
        Map<Double, Point> map = new HashMap<Double, Point>();

        double allDistanceSquareSum = 0;

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            double distanceSquareSum = 0;
            for (int j = 0; j < points.size(); j++) {
                distanceSquareSum += p.distanceSquare(points.get(j));
            }
            System.out.println(p.toString() + "\t" + distanceSquareSum);

            allDistanceSquareSum += distanceSquareSum;
            map.put(distanceSquareSum, p);
        }

        List<Point> result = new ArrayList<Point>();
        double thresholds = allDistanceSquareSum * FACTOR / map.size();
        System.out.println("allDistanceSquareSum:" + allDistanceSquareSum);
        System.out.println("avg:" + allDistanceSquareSum / map.size());
        System.out.println("thresholds:" + thresholds);
        for (Double item : map.keySet()) {
            if (item < thresholds) {
                result.add(map.get(item));
            }
        }

        return result;
    }
}
