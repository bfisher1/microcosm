package util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Triangle {
    IntLoc pointA;
    IntLoc pointB;
    IntLoc pointC;

    /**
     * Using method from https://www.mathopenref.com/coordtrianglearea.html
     */
    double getArea() {
        return Math.abs(pointA.getX() * (pointB.getY() - pointC.getY()) +
                pointB.getX() * (pointC.getY() - pointA.getY()) +
                pointC.getX() * (pointA.getY() - pointB.getY()) ) / 2.0;
    }
}
