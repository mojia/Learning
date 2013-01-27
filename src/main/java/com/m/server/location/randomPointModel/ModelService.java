package com.m.server.location.randomPointModel;

import java.util.ArrayList;
import java.util.List;

public class ModelService {

    public Point center(final List<Point> points) {
        // List<Point> result = sortPoint(points);
        // return getNPointCenter(result);

        return getArithematicMean(points);

        // return getGeometricMean(points);

        // return getAvgAreaCenter(result);
    }

    /**
     * 计算两个点的中点
     * 
     * @param points
     * @return
     */
    public Point get2PointCenter(Point... points) {
        double x = (points[0].getX() + points[1].getX()) / 2;
        double y = (points[0].getY() + points[1].getY()) / 2;
        return new Point(x, y);
    }

    /**
     * 几何平均
     * 
     * @param points
     * @return
     */
    public Point getGeometricMean(final List<Point> points) {
        if (points == null) {
            return null;
        }

        double xSum = 1.0;
        double ySum = 1.0;

        for (Point item : points) {
            xSum *= item.getX();
            ySum *= item.getY();
        }

        int n = points.size();
        double x = Math.pow(xSum, 1.0 / n);
        double y = Math.pow(ySum, 1.0 / n);

        return new Point(x, y);
    }

    /**
     * 算術平均數
     * 
     * @param points
     * @return
     */
    public Point getArithematicMean(final List<Point> points) {
        if (points == null) {
            return null;
        }

        double xSum = 0;
        double ySum = 0;

        for (int i = 0; i < points.size(); i++) {
            xSum += points.get(i).getX();
            ySum += points.get(i).getY();
        }

        return new Point(xSum / points.size(), ySum / points.size());
    }

    /**
     * 面心计算，不收敛
     * 
     * @param points
     * @return
     */
    public Point getAvgAreaCenter(final List<Point> points) {
        if (points == null) {
            return null;
        }
        if (points.size() == 0) {
            return null;
        } else if (points.size() == 1) {
            return points.get(0);
        } else if (points.size() == 2) {
            return get2PointCenter(points.get(0), points.get(1));
        }
        double x = 0, y = 0, area = 0;
        Point point0, point1;
        for (int i = 0; i < points.size() - 1; i++) {
            point0 = points.get(i);
            point1 = points.get(i + 1);
            area += (point0.getX() * point1.getY() - point1.getX()
                    * point0.getY()) / 2;
            x += (point0.getX() * point1.getY() - point1.getX() * point0.getY())
                    * (point0.getX() + point1.getX());
            y += (point0.getX() * point1.getY() - point1.getX() * point0.getY())
                    * (point0.getY() + point1.getY());
        }
        point0 = points.get(points.size() - 1);
        point1 = points.get(0);
        area += (point0.getX() * point1.getY() - point1.getX() * point0.getY()) / 2;
        x += (point0.getX() * point1.getY() - point1.getX() * point0.getY())
                * (point0.getX() + point1.getX());
        y += (point0.getX() * point1.getY() - point1.getX() * point0.getY())
                * (point0.getY() + point1.getY());
        return new Point(x / (6 * area), y / (6 * area));
    }

    /**
     * 将点列按照逆时针排序
     * 
     * @param points
     * @return
     */
    public List<Point> sortPoint(List<Point> points) {
        if (points == null || points.size() == 0) {
            return null;
        }

        List<Point> result = preparedList(points.size());

        int i, j, k = 0, top = 2;
        Point tmp = null;
        // 选取PointSet中y坐标最小的点PointSet[k]，如果这样的点右多个，则取最左边的一个
        for (i = 1; i < points.size(); i++) {
            if ((points.get(i).getY() < points.get(k).getY())
                    || ((points.get(i).getY() == points.get(k).getY()) && (points
                            .get(i).getX() < points.get(k).getX()))) {
                k = i;
            }
        }
        tmp = points.get(0);
        points.set(0, points.get(k));
        points.set(k, tmp); // 现在PointSet中y坐标最小的点在PointSet[0]

        // 对顶点按照相对PointSet[0]的极角从小到大进行排序，极角相同的按照距离PointSet[0]从近到远进行排序
        for (i = 1; i < points.size() - 1; i++) {
            k = i;
            for (j = i + 1; j < points.size(); j++)
                if ((multiply(points.get(j), points.get(k), points.get(0)) > 0)
                        || ((multiply(points.get(j), points.get(k),
                                points.get(0)) == 0) && (distance(
                                points.get(0), points.get(j)) < distance(
                                points.get(0), points.get(k))))) {
                    k = j;
                }
            tmp = points.get(i);
            points.set(i, points.get(k));
            points.set(k, tmp);
        }

        // 以上的代码将点集按照逆时针排序
        if (points.size() == 1) {
            result.set(0, points.get(0));
            return result;
        } else if (points.size() == 2) {
            result.set(0, points.get(0));
            result.set(1, points.get(1));
            return result;
        } else if (points.size() == 3) {
            result.set(0, points.get(0));
            result.set(1, points.get(1));
            result.set(2, points.get(2));
            return result;
        }
        result.set(0, points.get(0));
        result.set(1, points.get(1));
        result.set(2, points.get(2));

        for (i = 3; i < points.size(); i++) {
            while (top >= 1
                    && multiply(points.get(i), points.get(top),
                            points.get(top - 1)) >= 0) {
                top--;
            }
            ++top;
            result.set(top, points.get(i));
        }

        return result;
    }

    private List<Point> preparedList(int n) {
        List<Point> points = new ArrayList<Point>(n);
        for (int i = 0; i < n; i++) {
            points.add(new Point());
        }
        return points;
    }

    // 返回(P1-P0)*(P2-P0)的叉积。
    public double multiply(Point p1, Point p2, Point p0) {
        return (float) ((p1.getX() - p0.getX()) * (p2.getY() - p0.getY()) - (p2
                .getX() - p0.getY()) * (p1.getY() - p0.getY()));
    }

    // 求平面上两点之间的距离
    public double distance(Point p1, Point p2) {
        return (Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getY())
                + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY())));
    }

}
