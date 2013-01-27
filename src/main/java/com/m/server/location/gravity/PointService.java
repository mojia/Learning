package com.m.server.location.gravity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PointService {
    public static final ArrayList<Point> points = new ArrayList<Point>();
    private static final int MAX_WIDTH = 1200;
    private static final int MAX_HEIGHT = 900;
    private static final int SIDE_LENGTH = 200;
    private final static PointService service = new PointService();

    private static final Map<Integer, Double> errorMap = new HashMap<Integer, Double>();

    public static void main(final String[] args) {

        final JFrame jFrame = new JFrame();
        jFrame.setSize(MAX_WIDTH, MAX_HEIGHT);

        final JPanel jPanel = new JPanel() {
            private static final long serialVersionUID = -3543752374007484936L;

            @Override
            public void paint(final Graphics graphics) {
                graphics.setColor(Color.black);
                graphics.fillRect(0, 0, MAX_WIDTH, MAX_HEIGHT);
                graphics.setColor(Color.white);

                for (final Point p : points) {
                    final int x = (int) p.getX();
                    final int y = (int) p.getY();
                    // 将点(x,y)画上去
                    graphics.drawRoundRect(x, y, 3, 3, 2, 2);
                }

                drawWhatIlike(graphics);
            }
        };
        jPanel.setBackground(Color.black);
        jFrame.add(jPanel);
        jFrame.setVisible(true);

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            Point newPoint = CenterGravityModel.randomOnePoint();
            points.add(newPoint);
            SwingUtilities.updateComponentTreeUI(jPanel);
        }

        // p.addMouseListener(new MouseAdapter() {
        // @Override
        // public void mouseClicked(final MouseEvent e) {
        // final Point c = new Point(e.getX(), e.getY());
        // points.add(c);
        // SwingUtilities.updateComponentTreeUI(p);
        // }
        // });
    }

    static int count = 0;
    static StringBuffer result = new StringBuffer();

    protected static void drawWhatIlike(Graphics graphics) {
        Point target = new Point(0, 0);
        double targetDistance = 0;

        // // 画中心点
        // graphics.setColor(Color.YELLOW);
        // graphics.drawRoundRect(200, 200, 4, 4, 3, 3);

        // 画周围的10米的圆形
        graphics.setColor(Color.orange);
        graphics.fillOval(200, 200, 10, 10);

        if (points != null && points.size() != 0) {
            target = service.center(points);

            if (target != null) {
                Font font = new Font("Arial", 12, 14);
                graphics.setFont(font);
                graphics.setColor(Color.red);
                graphics.drawRoundRect((int) target.getX(),
                        (int) target.getY(), 5, 5, 3, 3);

                graphics.drawString("当前拟合的点：" + (int) target.getX() + ","
                        + (int) target.getY(), 50, 600);
                targetDistance = distance(new Point(SIDE_LENGTH, SIDE_LENGTH),
                        target);
                graphics.drawString(
                        "离中心点距离："
                                + new DecimalFormat("#.0000")
                                        .format(targetDistance), 200, 600);
                graphics.drawString("随机点的个数:" + points.size(), 50, 650);
                double error = Math.PI * Math.pow(targetDistance, 2.0) * 100.0
                        / (SIDE_LENGTH * SIDE_LENGTH * 4);
                graphics.drawString(
                        "误差为:"
                                + ((int) (Math.round(error * 1000000)) / 1000000.0)
                                + "%", 200, 650);

                graphics.drawLine(500, 750, 500, 0);
                graphics.drawLine(500, 750, 1200, 750);
                errorMap.put(count, error);
                graphics.setColor(Color.YELLOW);
                for (Integer x : errorMap.keySet()) {
                    int pointX = 500 + x;
                    int pointY = (int) (750 - errorMap.get(x) * 50);
                    graphics.drawRoundRect(pointX, pointY, 2, 2, 2, 2);
                }

                result.append((count++) + "," + targetDistance + "\n");
            }
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(
                    "/Users/wangxin09/Desktop/dis.txt")));
            writer.write(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Point get2PointCenter(Point... points) {
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
    public static Point getGeometricMean(final List<Point> points) {
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
    public static Point getArithematicMean(final List<Point> points) {
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

    public static double getRandomWeight() {
        int MAX_SCORE = 5;
        return MAX_SCORE * new Random().nextDouble();
    }

    /**
     * 面心计算，不收敛
     * 
     * @param points
     * @return
     */
    public static Point getAvgAreaCenter(final List<Point> points) {
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

    public Point center(final List<Point> points) {
        // List<Point> result = sortPoint(points);
        // return getNPointCenter(result);

        return getArithematicMean(points);

        // return getGeometricMean(points);

        // return getAvgAreaCenter(result);
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
        return (float) ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x)
                * (p1.y - p0.y));
    }

    // 求平面上两点之间的距离

    public static double distance(Point p1, Point p2) {
        return (Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
                * (p1.y - p2.y)));
    }

}
