package world.block;

import playground.World;

public class TrayBlock extends Block {
    public TrayBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("tray.png");
    }
}
