package world;

import lombok.Getter;
import lombok.Setter;
import util.IntLoc;
import util.Loc;
import world.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class World {
    private Map<IntLoc, Chunk> chunks;

    private double x;
    private double y;

    public World(double x, double y) {
        this.x = x;
        this.y = y;

        int startIdx = (int) (-x / Block.BLOCK_WIDTH) / Chunk.CHUNK_SIZE;
        int startIdy = (int) (-y / Block.BLOCK_WIDTH) / Chunk.CHUNK_SIZE;

        chunks = new HashMap<>();
        for(int i = startIdx - 2; i < 2 + startIdx; i++) {
            for(int j = startIdy - 2; j < 2 + startIdy; j++) {
                IntLoc loc = new IntLoc(i, j);
                chunks.put(loc, new Chunk(i, j, this));
            }
        }
        chunks.forEach((loc, chunk) -> {
            chunk.load();
        });
    }

    public void loadChunk(int xId, int yId) {
        IntLoc loc = new IntLoc(xId, yId);
        chunks.put(loc, new Chunk(xId, yId, this));
        chunks.get(loc).load();
    }

    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        chunks.forEach((chunkLoc, chunk) -> {
            chunk.getBlocks().forEach((loc, block) -> {
                blocks.add(block);
            });
        });
        System.out.println("Block lens: " + blocks.size());
        return blocks;
    }

    public void move(double x, double y) {
        this.x += x;
        this.y += y;

        getAllBlocks().forEach(block -> {
            if(block.getEntity() != null)
                block.move(x, y);
        });
    }


}
