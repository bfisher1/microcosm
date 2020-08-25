package world.structure;

import item.Item;
import microcosm.BlockSaver;
import util.IntLoc;
import world.Chunk;
import world.World;
import world.block.Block;
import world.block.BlockFactory;
import world.block.TreadmillBlock;

import java.util.*;
import java.util.stream.Collectors;

public class StructureGenerator {


    Map<Character, Block.Type> key = new HashMap<>();

    public StructureGenerator() {
        key.put('g', Block.Type.Grass);
        key.put('w', Block.Type. Water);
        key.put('c', Block.Type.Coal);
        key.put('s', Block.Type.Stone);
        key.put('S', Block.Type.Sand);
        key.put('t', Block.Type.Tree);
        key.put('O', Block.Type.Copper);
        key.put('z', Block.Type.Zinc);
        key.put('I', Block.Type.Silicon);
        key.put('n', Block.Type.Nickel);
        key.put('i', Block.Type.Iron);
        key.put('c', Block.Type.Sun);
        key.put('u', Block.Type.Uranium);
        key.put('w', Block.Type.Wire);
        key.put('G', Block.Type.Generator);
        key.put('p', Block.Type.Plutonium);
        key.put('T', Block.Type.Treadmill);
        key.put('/', Block.Type.Injector);
    }


    public void generateFactoryV1(World world, int x, int y, boolean stack) {
        String rows[] = {
                "T",
                "/",
                "TwwwwwG",
                "T",
                "T",
                "T",
                "T",
                "T",
                "T",
        };

        saveBlocks(world, stringsToBlock(rows, x, y, world), stack);
    }

    private void saveBlocks(World world, List<Block> blocks, boolean stack) {
        if (stack) {
            List<Chunk> chunks = new ArrayList<>();
            // get all chunks
            blocks.stream().forEach(block -> {
                IntLoc loc = world.getChunkLocForBlockAt(block.getX(), block.getY());
                Chunk chunkAt = new Chunk(loc.getX(), loc.getY(), world);
                // if there isn't a chunk at this location, add it
                if (chunks.stream().filter(chunk -> chunk.equals(chunkAt)).collect(Collectors.toList()).isEmpty()) {
                    chunks.add(chunkAt);
                }
            });

            int worldChunkRadius = world.getRadius() / Chunk.CHUNK_SIZE + 1;

            // TEMPORARY load other blocks in world
            for(int x = -worldChunkRadius; x < worldChunkRadius; x++) {
                for(int y = -worldChunkRadius; y < worldChunkRadius; y++) {
                    Chunk chunkAt = new Chunk(x, y, world);
                    if (chunks.stream().filter(chunk -> chunk.equals(chunkAt)).collect(Collectors.toList()).isEmpty()) {
                        chunks.add(chunkAt);
                    }
                }
            }


            chunks.stream().forEach(chunk -> {
                world.getChunks().put(new IntLoc(chunk.getXId(), chunk.getYId()), chunk);
                chunk.load();
            });

            blocks.forEach(block -> {
                if (world.isBlockLoaded(block.getX(), block.getY())) {
                    // why isn't the stacking working here?
                    world.getBlockAt(block.getX(), block.getY()).stack(block);
                }
            });

            //BlockSaver.add(blocks);


            new BlockSaver(true).run();


        } else {
            // TODO, need to delete previous blocks from location and generate
        }
    }

    private List<Block> stringsToBlock(String rows[], int startX, int startY, World world) {
        List<Block> blocks = new ArrayList<>();
        for(int j = 0; j < rows.length; j++) {
            for(int i = 0; i < rows[j].length(); i++) {
                Block.Type type = key.get(rows[j].charAt(i));
                blocks.add(BlockFactory.create(i + startX, j + startY, type, world));
            }
        }
        return blocks;
    }
}
