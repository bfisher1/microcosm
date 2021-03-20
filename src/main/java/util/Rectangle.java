package util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Rectangle on screen in any location, used to see if coordinates are in it.
 *
 *          A           B
 *          D           C
 */
@Getter
@Setter
@AllArgsConstructor
public class Rectangle {
    IntLoc pointA;
    IntLoc pointB;
    IntLoc pointC;
    IntLoc pointD;

    /**
     * Using method from https://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle
     */
    public boolean isInside(double x, double y) {
        IntLoc pointP = new IntLoc(x, y);
        double apdArea = (new Triangle(pointA, pointP, pointD)).getArea();
        double dpcArea = (new Triangle(pointD, pointP, pointC)).getArea();
        double cpbArea = (new Triangle(pointC, pointP, pointB)).getArea();
        double pbaArea = (new Triangle(pointP, pointB, pointA)).getArea();
        double rectArea = getArea();

        return apdArea + dpcArea + cpbArea + pbaArea <= rectArea;
    }

    public List<IntLoc> getPoints() {
        List<IntLoc> points = new ArrayList<>();
        points.add(pointA);
        points.add(pointB);
        points.add(pointC);
        points.add(pointD);
        return points;
    }

    private double getArea() {
        return (new Triangle(pointA, pointB, pointC)).getArea() + (new Triangle(pointA, pointD, pointC)).getArea();
    }
}
