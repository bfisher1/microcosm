package util;

import world.block.Block;

import java.util.Random;

public class Rand {

    public static double randDoubleWithMaxAbsValue(int max) {
        return (new Random()).nextDouble() * max * 2 - max;
    }

    public static Loc randomLoc(int maxX, int maxY) {
        double x = randDoubleWithMaxAbsValue(maxX);
        double y = randDoubleWithMaxAbsValue(maxY);
        return new Loc(x, y);
    }

    public static IntLoc randomIntLoc(int maxX, int maxY) {
        double x = randDoubleWithMaxAbsValue(maxX);
        double y = randDoubleWithMaxAbsValue(maxY);
        return new IntLoc(x, y);
    }

    public static IntLoc randomIntLocCenteredAtZero(int xRange, int yRange) {
        double x = 0;// randDoubleWithMaxAbsValue(xRange) - xRange / 2;
        double y = 0; //randDoubleWithMaxAbsValue(yRange) - yRange / 2;
        return new IntLoc(x, y);
    }

    public static double randDouble() {
        return (new Random()).nextDouble();
    }

}
