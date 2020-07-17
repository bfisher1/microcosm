package world.block;

import world.World;

public class StoneBlock extends Block {
    public StoneBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("stone.png");
    }
}
