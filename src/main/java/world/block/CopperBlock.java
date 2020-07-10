package world.block;

import world.World;

public class CopperBlock extends Block {
    public CopperBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimName("copper.png");
    }
}
