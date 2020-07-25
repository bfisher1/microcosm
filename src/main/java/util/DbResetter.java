package util;

import world.World;

/**
 * Resets the database with sample worlds.
 */
public class DbResetter {
    public static void main(String[] args) {
        clearDb();
        fillDb();
    }

    private static void clearDb() {
        // TODO figure out better deletion strategy
        DbClient.deleteAll(World.class);
    }

    private static void fillDb() {
        World world = new World(0, 0);
        DbClient.save(world);
    }
}
