package world.block;

import playground.World;

public class PrinterBlock extends Block {
    public PrinterBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("printer.png");
    }
}
