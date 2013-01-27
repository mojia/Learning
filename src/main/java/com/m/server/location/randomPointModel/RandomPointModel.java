package com.m.server.location.randomPointModel;

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
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RandomPointModel {
    // 随机点容器
    public static final ArrayList<Point> points = new ArrayList<Point>();
    // 画布宽
    private static final int MAX_WIDTH = 1200;
    // 画布长
    private static final int MAX_HEIGHT = 900;
    // 中心点取定边长
    private static final int SIDE_LENGTH = 200;

    private final static ModelService service = new ModelService();
    // 误差容器
    private static final Map<Integer, Double> errorMap = new HashMap<Integer, Double>();

    private static int count = 0;
    // 要记录到文件的内容
    private static StringBuffer content = new StringBuffer();

    public static void main(final String[] args) {
        final RandomPointModel model = new RandomPointModel();
        // 将模型画出来
        drawModel(model);
    }

    private static void drawModel(final RandomPointModel model) {
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
                    // 将随机点(x,y)画上去
                    graphics.drawRoundRect(x, y, 3, 3, 2, 2);
                }

                model.drawWhatIlike(graphics);
            }
        };
        jPanel.setBackground(Color.black);
        jFrame.add(jPanel);
        jFrame.setVisible(true);

        // 每间隔0.5秒生成一个随机点，随机点位于(0,0),(0,400),(400,400),(400,0)围成的矩形中
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            Point newPoint = RandomHelper.randomOnePoint();
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

    /**
     * 添加个性化的东西
     * 
     * @param graphics
     */
    private void drawWhatIlike(Graphics graphics) {
        // 算法拟合的点
        Point target = new Point(0, 0);
        // 画中心点
        graphics.setColor(Color.orange);
        graphics.fillOval(200, 200, 10, 10);

        if (points != null && points.size() != 0) {
            target = service.center(points);

            drawTarget(graphics, target);
        }
        saveFile(content);
    }

    private void drawTarget(Graphics graphics, Point target) {
        // 拟合点与中心点的距离
        double targetDistance = 0;
        if (target != null) {
            Font font = new Font("Arial", 12, 14);
            graphics.setFont(font);
            graphics.setColor(Color.red);
            graphics.drawRoundRect((int) target.getX(), (int) target.getY(), 5,
                    5, 3, 3);
            drawInstruction(graphics, target, targetDistance);

            content.append((count++) + "," + targetDistance + "\n");
        }
    }

    /**
     * 添加一些说明行的文字
     * 
     * @param graphics
     * @param target
     * @param targetDistance
     */
    private void drawInstruction(Graphics graphics, Point target,
            double targetDistance) {
        graphics.drawString("当前拟合的点：" + (int) target.getX() + ","
                + (int) target.getY(), 50, 600);
        targetDistance = service.distance(new Point(SIDE_LENGTH, SIDE_LENGTH),
                target);
        graphics.drawString(
                "离中心点距离：" + new DecimalFormat("#.0000").format(targetDistance),
                200, 600);
        graphics.drawString("随机点的个数:" + points.size(), 50, 650);
        double error = Math.PI * Math.pow(targetDistance, 2.0) * 100.0
                / (SIDE_LENGTH * SIDE_LENGTH * 4);
        graphics.drawString("误差为:"
                + ((int) (Math.round(error * 1000000)) / 1000000.0) + "%", 200,
                650);

        drawError(graphics, error);
    }

    /**
     * 画误差坐标信息
     * 
     * @param graphics
     * @param error
     */
    private void drawError(Graphics graphics, double error) {

        graphics.drawLine(500, 750, 500, 0);
        graphics.drawLine(500, 750, 1200, 750);
        errorMap.put(count, error);
        graphics.setColor(Color.YELLOW);
        for (Integer x : errorMap.keySet()) {
            int pointX = 500 + x;
            int pointY = (int) (750 - errorMap.get(x) * 50);
            graphics.drawRoundRect(pointX, pointY, 2, 2, 2, 2);
        }
    }

    /**
     * 保存需要记录的文件
     * 
     * @param content2
     */
    private static void saveFile(StringBuffer content2) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(
                    "/Users/wangxin09/Desktop/dis.txt")));
            writer.write(content.toString());
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

}
