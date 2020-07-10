package world.block;

import world.World;

public class UnknownBlock extends Block {
    public UnknownBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimName("unknown.png");
    }
}
