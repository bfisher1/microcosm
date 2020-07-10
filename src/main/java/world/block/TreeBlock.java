package world.block;

import world.World;

public class TreeBlock extends Block {
    public TreeBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimName("wizard_anims/tree.png");
    }
}
