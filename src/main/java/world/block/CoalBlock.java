package world.block;

import world.World;

public class CoalBlock extends Block {
    public CoalBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("coal.png");
    }
}
