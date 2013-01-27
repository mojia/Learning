package com.m.server.location.gravity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Position;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * 混合定位服务，该服务结合wifi定位和基站定位，苛求最佳定位效果
 * 
 * @author wangxin09
 * @created 2013-1-18
 * 
 * @version 1.0
 */
public class CenterGravityService {
    private Log log = LogFactory.getLog("compoundlocation");

    private static final double MAX_FACTOR = 1.4;
    private static final double MIN_FACTOR = 0.55;
    private static final int DEFAULT_CELL_TOWER_ACCURACY = 200;

    /**
     * 根据每个基站(基站个数大于或者等于1)的定位出来的位置以及其信号强度，估算出最终的目标点的经度，纬度。
     * 这个地方有多种算法来实现。
     * 采用的算法就是找到多个基站的重心
     * 此处返回坐标Coordinates对象只包含GPS坐标信息。
     * 
     * @param towers
     * @return
     */
    public Point estimateTargetPoint(List<Point> points) {
        switch (points.size()) {
        case 0:
            return null;
        case 1:
            return points.get(0);
        case 2:
            return estimateTargetFromTwoPoints(points);
        default:
            return estimateTargetPointFromMutilPoint(points);
        }
    }

    /**
     * 取两个点的中心作为目标点返回
     * 
     * @param points
     * @return
     */
    private Point estimateTargetFromTwoPoints(List<Point> coords) {
        double latCoord = (coords.get(0).getX() + coords.get(1).getX()) / 2;
        double lngCoord = (coords.get(0).getY() + coords.get(1).getY()) / 2;

        Point coord = new Point();
        coord.setX(latCoord);
        coord.setY(lngCoord);

        return coord;
    }

    /**
     * 根据多个点的GPS纬度，经度，估算出目标点的GPS纬度，经度。
     * 
     * @param towers
     * @return
     */
    public Point estimateTargetPointFromMutilPoint(List<Point> points) {
        // 清除噪音点
        List<Point> candidates = removeNoiseCoordinates(points);
        if (candidates == null || candidates.size() == 0) {
            return null;
        } else if (candidates.size() == 1) {
            return candidates.get(0);
        } else if (candidates.size() == 2) {
            return estimateTargetFromTwoPoints(candidates);
        }

        // 从保留的多个点中计算重心点
        Point center = computeCenterOfGravityOfCandidates(candidates);

        Point targetCoord = new Point();
        targetCoord.setX(center.getX());
        targetCoord.setY(center.getY());

        return targetCoord;
    }

    /**
     * 计算坐标点的重心
     * 
     * @param candidates
     * @return
     */
    public Point computeCenterOfGravityOfCandidates(List<Point> candidates) {
        double sumLat = 0;
        double sumLng = 0;
        double sumArea = 0;
        double area;
        Point firstPoint = candidates.get(0);
        Point secondPoint = candidates.get(1);

        double targetX = 0;
        double targetY = 0;

        for (int i = 2; i < candidates.size(); i++) {
            Point item = candidates.get(i);
            area = computeTriangleArea(firstPoint, secondPoint, item);
            sumArea += area;
            sumLat += (firstPoint.getX() + secondPoint.getX() + item.getX())
                    * area;
            sumLng += (firstPoint.getY() + secondPoint.getY() + item.getY())
                    * area;

            targetX = sumLat / sumArea / 3;
            targetY = sumLng / sumArea / 3;

            System.out.println("targetX=" + targetX + "," + "targetY="
                    + targetY);

            secondPoint = item;

        }

        Point center = new Point();
        center.setX(targetX);
        center.setY(targetY);

        return center;
    }

    /**
     * 除去噪音点
     * 
     * @param points
     * @return
     */
    private List<Point> removeNoiseCoordinates(List<Point> points) {
        Map<Double, Point> map = new HashMap<Double, Point>(10);

        double allDistanceSquareSum = 0;

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            double distanceSquareSum = 0;
            for (int j = 0; j < points.size(); j++) {
                distanceSquareSum += computeTwoCoordinatesDistance(p,
                        points.get(j));
            }

            allDistanceSquareSum += distanceSquareSum;
            map.put(distanceSquareSum, p);
        }

        List<Point> result = new ArrayList<Point>();
        // 阀值
        double maxThresholds = allDistanceSquareSum * MAX_FACTOR / map.size();
        double minThresholds = allDistanceSquareSum * MIN_FACTOR / map.size();

        for (Double item : map.keySet()) {
            // 落在阀值区间的点留住
            if (item < maxThresholds && item > minThresholds) {
                result.add(map.get(item));
            }
        }

        return result;
    }

    /**
     * 计算两坐标点的距离.
     * 
     * @param first
     * @param second
     * @return
     */
    public static double computeTwoCoordinatesDistance(Point first, Point second) {
        double x0 = first.getX();
        double y0 = first.getY();

        double x1 = second.getX();
        double y1 = second.getY();

        return Math.pow(x1 - x0, 2.0) + Math.pow(y1 - y0, 2.0);
    }

    /**
     * 计算三角形的面积
     * 
     * @param firstPoint
     * @param secondPoint
     * @param item
     * @return
     */
    private double computeTriangleArea(Point firstPoint, Point secondPoint,
            Point thirdPoint) {
        double area = 0;
        area = firstPoint.getX() * secondPoint.getY() + secondPoint.getX()
                * thirdPoint.getY() + thirdPoint.getX() * firstPoint.getY()
                - secondPoint.getX() * firstPoint.getY() - thirdPoint.getX()
                * secondPoint.getY() - firstPoint.getX() * thirdPoint.getY();

        return area / 2;
    }

}
