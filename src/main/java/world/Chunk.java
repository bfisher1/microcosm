package world;

import lombok.Getter;
import lombok.Setter;
import util.IntLoc;
import world.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Chunk {
    public static int CHUNK_SIZE = 10;

    private int xId;
    private int yId;
    private Map<IntLoc, Block> blocks;
    private World world;

    public Chunk(int xId, int yId, World world) {
        this.xId = xId;
        this.yId = yId;
        this.blocks = new HashMap<>();
        this.world = world;
    }

    public void load() {
        boolean chunkHasBeenLoadedBefore = false;
        if(chunkHasBeenLoadedBefore) {
            // load from persistent memory
        } else {
            // generate using perlin noise
            blocks = WorldGenerator.generateBlocks(xId * CHUNK_SIZE, yId * CHUNK_SIZE, xId * CHUNK_SIZE + CHUNK_SIZE, yId * CHUNK_SIZE + CHUNK_SIZE, world);
            blocks.forEach((loc, block) -> {
                block.loadOnScreen();
                block.move(world.getX(), world.getY());
            });
        }
    }
}