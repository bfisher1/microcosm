package util;

public class MathUtil {
    public static boolean within(double num1, double num2, double threshold) {
        return Math.abs(num1 - num2) <= threshold;
    }

    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double dist(Loc p1, Loc p2) {
        return dist(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
}
