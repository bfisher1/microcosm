package world;

import lombok.Getter;
import lombok.Setter;
import player.Camera;
import util.IntLoc;
import world.block.Block;

import java.util.*;

@Getter
@Setter
public class World {
    private Map<IntLoc, Chunk> chunks;

    private double x;
    private double y;
    private List<Camera> cameras;
    private static int WORLD_COUNT = 0;
    private static int id;

    public World(double x, double y) {
        this.x = x;
        this.y = y;
        cameras = new ArrayList<>();
        chunks = new HashMap<>();
        id = WORLD_COUNT;
        WORLD_COUNT++;
    }

    public void loadInitialChunks() {
        int START_CHUNKS = 7;
        int startIdx = (int) (-x / Block.BLOCK_WIDTH) / Chunk.CHUNK_SIZE;
        int startIdy = (int) (-y / Block.BLOCK_WIDTH) / Chunk.CHUNK_SIZE;
        for(int i = startIdx - START_CHUNKS; i < START_CHUNKS + startIdx; i++) {
            for(int j = startIdy - START_CHUNKS; j < START_CHUNKS + startIdy; j++) {
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
        //System.out.println("Block lens: " + blocks.size());
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

    public Chunk getChunkForBlockAt(int x, int y) {
        // Chunks are stored 0,0 is x=-10 to -1  0,0 is x=0-9, y=0-9
        if( x < 0)
            x -= Chunk.CHUNK_SIZE + 1;
        if(y < 0)
            y -= Chunk.CHUNK_SIZE + 1;

        IntLoc loc = new IntLoc(x / Chunk.CHUNK_SIZE, y / Chunk.CHUNK_SIZE);
        if (chunks.containsKey(loc))
            return chunks.get(loc);
        throw new IllegalArgumentException("No chunk for block at " + loc);
    }

    public boolean isBlockLoaded(int x, int y) {
        try {
            getBlockAt(x, y);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public Block getBlockAt(int x, int y) {
        // goto chunk map, grab block from there
        try {
            Chunk chunk = getChunkForBlockAt(x, y);
            Block block = chunk.getBlocks().get(new IntLoc(x, y));
            if(block == null) {
                throw new IllegalArgumentException("Could not find block at " + x + ", " + y);
            }
            return block;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find block at " + x + ", " + y);
        }
    }

    public Block replaceBlockAt(int x, int y, Block newBlock) {
        // goto chunk map, grab block from there
        try {
            IntLoc loc = new IntLoc(x, y);
            Chunk chunk = getChunkForBlockAt(x, y);
            Block oldBlock = chunk.getBlocks().get(loc);
            chunk.getBlocks().put(loc, newBlock);
            // TEMPORARY, move this elsewhere or TODO make a sprite update method
            newBlock.addToScreen(cameras.get(0));
            oldBlock.removeFromScreen();
            return oldBlock;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not set block at " + x + ", " + y);
        }
    }

    public void addRenderedBlock(Block block) {
        cameras.forEach(camera -> {
            camera.addRenderedBlock(block);
        });
    }

    public void removeRenderedBlock(Block block) {
        cameras.forEach(camera -> {
            camera.removeRenderedBlock(block);
        });
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof World) {
            World otherWorld = (World) other;
            return otherWorld.id == id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
