package playground;

import util.MathUtil;
import world.block.Block;

public class Sun extends World {

    public Sun(double x, double y, int radius) {
        setRadius(radius);
        setX(x);
        setY(y);
        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                if (MathUtil.dist(0, 0, i, j) <= radius) {
                    createBlockAt(i, j, 0, Block.Type.Sun);
                }
            }

        }
    }

}
