package world.block;

import world.World;

public class SandBlock extends Block {
    public SandBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("sand.png");
    }
}
