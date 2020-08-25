package world.resource;

import item.Itemable;
import world.block.Block;

public class ResourceCost {
    public static int getFuelAmount(Itemable item) {
        Object type = item.getType();
        if (Block.Type.Coal.equals(type)) {
            return 1;
        }
        // TODO maybe add oil
        else if (Block.Type.Tree.equals(type)) {
            return 2;
        }
        return 0;
    }

    public static boolean isFuel(Itemable item) {
        return getFuelAmount(item) != 0;
    }

    public static boolean isInjectorMaterial(Itemable item) {
        Object type = item.getType();
        return Block.Type.Iron.equals(type) || Block.Type.Copper.equals(type);
    }
}
