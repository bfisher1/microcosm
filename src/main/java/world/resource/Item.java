package world.resource;

import animation.Animation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.Vector;
import world.block.Block;

/**
 * Abstract because an item has to exist somewhere (i.e. an inventory, a block, outer space)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Item {
    private Animation animation;
    private double quantity;

    // TODO, eventually calculate this by analyzing animation for widest/tallest lines of non-alpha pixels
    private Vector sizePx = new Vector(Block.BLOCK_WIDTH, Block.BLOCK_WIDTH);

    public abstract String getType();
}
