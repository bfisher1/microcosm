package world.block;

import world.World;

public class NickelBlock extends Block {
    public NickelBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimName("nickel.png");
    }
}
