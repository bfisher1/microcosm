package world.block;

import world.World;

public class SiliconBlock extends Block {
    public SiliconBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimName("silicon.png");
    }
}
