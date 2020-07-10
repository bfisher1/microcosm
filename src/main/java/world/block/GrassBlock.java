package world.block;

import world.World;

public class GrassBlock extends Block {
    public GrassBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimName("red-dirt.png");
    }
}
