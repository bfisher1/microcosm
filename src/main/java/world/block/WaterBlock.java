package world.block;

import world.World;

public class WaterBlock extends Block {
    public WaterBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimName("green-water.png");
    }
}
