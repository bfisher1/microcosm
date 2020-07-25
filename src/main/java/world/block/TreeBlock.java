package world.block;

import util.Loc;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Random;

@Entity
@Table(name = "tree_block")
public class TreeBlock extends Block {

    public static int SPRITE_OFFSET_MAX = BLOCK_WIDTH / 2;

    public TreeBlock(int x, int y, World world) {
        super(x, y, world);
        double rand = (new Random()).nextDouble();
        if (rand < .5)
            setAnimation("wizard_anims/tree.png");
        else
            setAnimation("tree-big.png");

        setXSpriteOffset((new Random()).nextInt(SPRITE_OFFSET_MAX) - SPRITE_OFFSET_MAX / 2);
        setYSpriteOffset((new Random()).nextInt(SPRITE_OFFSET_MAX) - SPRITE_OFFSET_MAX / 2 - 100);

    }

    public void setScreenLoc(Loc loc) {
        super.setScreenLoc(loc);
        getEntity().setZ(getZ() + (int) getEntity().getY());
    }

}
