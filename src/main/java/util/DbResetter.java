package util;

import world.Sun;
import world.World;
import world.block.Block;

/**
 * Resets the database with sample worlds.
 */
public class DbResetter {
    public static void main(String[] args) {
        clearDb();
        fillDb();
        System.exit(0);
    }

    private static void clearDb() {
        // TODO figure out better deletion strategy
        DbClient.deleteAll(World.class);
    }

    private static void fillDb() {
        World world = new World(0, 0);
        Sun sun = new Sun(25 * Block.BLOCK_WIDTH, 10);
        DbClient.save(world);
        DbClient.save(sun);
    }
}
