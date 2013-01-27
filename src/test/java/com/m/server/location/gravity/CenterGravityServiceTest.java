package com.m.server.location.gravity;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class CenterGravityServiceTest {

    @Test
    public void testMutilPoints() {
        CenterGravityService service = new CenterGravityService();
        List<Point> points = preparedList();
        service.computeCenterOfGravityOfCandidates(points);
    }

    private List<Point> preparedList() {
        return Arrays.asList(new Point(16, 5), new Point(10, 16), new Point(1,
                2), new Point(3, 10), new Point(5, 2), new Point(8, 9),
                new Point(18, 2), new Point(2, 8));
    }

}
