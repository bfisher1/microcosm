package world.block;

import playground.World;

public class ClayBlock extends Block {
    public ClayBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("clay.png");
    }
}
