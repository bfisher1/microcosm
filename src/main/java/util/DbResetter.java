package util;

import world.Sun;
import world.World;
import world.block.Block;
import world.block.TreadmillBlock;
import world.structure.StructureGenerator;

/**
 * Resets the database with sample worlds.
 */
public class DbResetter {
    public static void main(String[] args) {
        System.out.println("Clearing database...");
        clearDb();
        System.out.println("Filling database...");
        fillDb();
        System.exit(0);
    }

    private static void clearDb() {
        // TODO figure out better deletion strategy
        DbClient.deleteAll(World.class);
    }

    private static void fillDb() {
        World world = new World(0, 0);
        Sun sun = new Sun(65 * Block.BLOCK_WIDTH, 10);
        DbClient.save(world);
        DbClient.save(sun);

        System.out.println("Generating Structures...");

        StructureGenerator generator = new StructureGenerator();
        generator.generateFactoryV1(world, 0, 0, true);


    }
}
