package playground;

import util.MathUtil;
import world.block.Block;

public class Sun extends World {

    double tempTime = 0.0;
    double revolveRadius;

    public Sun(double x, double y, int radius, Block.Type type) {
        setRadius(radius);
        setX(x);
        setY(y);
        revolveRadius = x;
        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                if (MathUtil.dist(0, 0, i, j) <= radius) {
                    createBlockAt(i, j, 0, type);
                }
            }

        }
    }

    // todo - temporary, remove
    public void revolve() {
        tempTime += .028;
        this.setRotationSpeed(0.0);
        this.setX(revolveRadius * Math.cos(tempTime));
        this.setY(revolveRadius * Math.sin(tempTime));
    }

    public Sun(double x, double y, int radius) {
        this(x, y, radius, Block.Type.Sun);
    }

}
