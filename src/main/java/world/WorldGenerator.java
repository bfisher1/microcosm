package world;

import util.IntLoc;
import world.block.Block;
import world.block.BlockFactory;

import java.util.HashMap;
import java.util.Map;

public class WorldGenerator {

    private static double ORE_CUTOFF = .84;

    public static Map<IntLoc, Block> generateBlocks(int startX, int startY, int endX, int endY, World world) {
        Map<IntLoc, Block> blocks = new HashMap<>();
        if (world instanceof Sun) {
            for(int x = startX; x < endX; x++) {
                for(int y = startY; y < endY; y++) {
                    if(Math.sqrt(x  * x + y * y) <= world.getRadius())
                        blocks.put(new IntLoc(x, y), BlockFactory.create(x, y, Block.Type.Sun, world));
                }
            }
            return blocks;
        }

        for(int x = startX; x < endX; x++) {
            for(int y = startY; y < endY; y++) {
                if(Math.sqrt(x  * x + y * y) <= world.getRadius())
                    blocks.put(new IntLoc(x, y), BlockFactory.create(x, y, Block.Type.Grass, world));
            }
        }

        for(int x = startX; x < endX; x++) {
            for(int y = startY; y < endY; y++) {
                if(Math.sqrt(x  * x + y * y) <= world.getRadius()) {
                    createResources(blocks, Block.Type.Stone, Block.Type.Grass, .54, 1, .04, 8, world);
                    createResources(blocks, Block.Type.Sand, Block.Type.Grass, .55, 3, .04, 8, world);
                    createResources(blocks, Block.Type.Water, Block.Type.Sand, .65, 3, .04, 8, world);

                    createResources(blocks, Block.Type.Tree, Block.Type.Grass, .65, 4, .04, 8, world, true);

                    createResources(blocks, Block.Type.Coal, Block.Type.Stone, ORE_CUTOFF, 5, .05125, 8, world);
                    createResources(blocks, Block.Type.Copper, Block.Type.Stone, ORE_CUTOFF, 6, .05125, 8, world);
                    createResources(blocks, Block.Type.Zinc, Block.Type.Stone, ORE_CUTOFF, 7, .05125, 8, world);
                    createResources(blocks, Block.Type.Silicon, Block.Type.Stone, ORE_CUTOFF, 8, .05125, 8, world);
                    createResources(blocks, Block.Type.Nickel, Block.Type.Stone, ORE_CUTOFF, 9, .05125, 8, world);
                    createResources(blocks, Block.Type.Iron, Block.Type.Stone, ORE_CUTOFF, 10, .05125, 8, world);
                }
            }
        }
        return blocks;
    }

    private static void createResources(Map<IntLoc, Block> blocks, Block.Type createdType, Block.Type surroundingType, double heightCutoff, int seed, double freq, int depth, World world) {
        createResources(blocks, createdType, surroundingType, heightCutoff, seed, freq, depth, world, false);
    }

    private static void createResources(Map<IntLoc, Block> blocks, Block.Type createdType, Block.Type surroundingType, double heightCutoff, int seed, double freq, int depth, World world, boolean stack) {
        blocks.forEach((loc, block) -> {
            double height = PerlinNoise.getHeight(loc.getX(), loc.getY(), freq, depth, seed);
            if(height > heightCutoff && blocks.get(loc).getType().equals(surroundingType)) {
                if (!stack) {
                    blocks.put(loc, BlockFactory.create(loc.getX(), loc.getY(), createdType, world));
                }
                else {
                    blocks.get(loc).stack(BlockFactory.create(loc.getX(), loc.getY(), createdType, world));
                }
            }
        });
    }

}
