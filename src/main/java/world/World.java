package world;

import lombok.Getter;
import lombok.Setter;
import player.Camera;
import util.IntLoc;
import world.block.Block;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "world")
//@NoArgsConstructor
public class World {

    public enum Type {
        World,
        Sun,
        Moon,
        Asteroid
    }

    public static List<World> loadedWorlds = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private Map<IntLoc, Chunk> chunks;
    @Transient
    private Map<Block.Type, List<Block>> blocksByType;

    private double x;
    private double y;

    private static long WORLD_COUNT = 0;

    private int radius;

    private Type type;

    public World() {
        id = WORLD_COUNT;

        chunks = new HashMap<>();
        blocksByType = new HashMap<>();
        radius = 20;
        type = Type.World;
    }

    public World(double x, double y) {
        this();
        this.x = x;
        this.y = y;
    }

    public void loadInitialChunks() {
        int START_CHUNKS = 2;//4;
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
            if(block.getSprite() != null)
                block.move(x, y);
        });
    }

    public List<Block> getLoadedBlocksOfType(Block.Type type) {
        if (blocksByType.containsKey(type)) {
            return blocksByType.get(type);
        }
        return new ArrayList<>();
    }

    public IntLoc getChunkLocForBlockAt(int x, int y) {
        // Chunks are stored 0,0 is x=-10 to -1  0,0 is x=0-9, y=0-9
        if( x < 0)
            x =  x - Chunk.CHUNK_SIZE + 1;
        if(y < 0)
            y = y - Chunk.CHUNK_SIZE + 1;

        IntLoc loc = new IntLoc(x / Chunk.CHUNK_SIZE, y / Chunk.CHUNK_SIZE);
        return loc;
    }

    public Chunk getChunkForBlockAt(int x, int y) {
        IntLoc loc = getChunkLocForBlockAt(x, y);
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

    public Block getBlockAt(int x, int y, int z) {
        Block block = getBlockAt(x, y);
        for(int i = 1; i < z; i++) {
            if (block == null) {
                throw new IllegalArgumentException("Could not find block at " + x + ", " + y + ", " + z);
            }
            block = block.getAbove();
        }
        return block;
    }

    public Block replaceBlockAt(int x, int y, Block newBlock) {
        // goto chunk map, grab block from there
        try {
            IntLoc loc = new IntLoc(x, y);
            Chunk chunk = getChunkForBlockAt(x, y);
            Block oldBlock = chunk.getBlocks().get(loc);
            chunk.getBlocks().put(loc, newBlock);
            // TEMPORARY, move this elsewhere or TODO make a sprite update method
            newBlock.addToScreen(Camera.getInstance());
            oldBlock.removeFromScreen();
            return oldBlock;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not set block at " + x + ", " + y);
        }
    }

    public void addRenderedBlock(Block block) {
        Camera.getInstance().addRenderedBlock(block);
    }

    public void removeRenderedBlock(Block block) {
        Camera.getInstance().removeRenderedBlock(block);
    }


    //@Cacheable(value = "nearbyWorlds", key=this.id)
    public List<World> getNearbyWorlds() {
        // TODO add distance threshold and filter out worlds past that
        //return DbClient.findAll(World.class).stream().filter(world -> world.getId() != this.id).collect(Collectors.toList());
        return loadedWorlds.stream().filter(world -> world != this).collect(Collectors.toList());
    }



    public double temperatureOutput() {
        double temp = numberOfBlocksOfType(Block.Type.Sun) * 6
                + numberOfBlocksOfType(Block.Type.Uranium) * 2
                + numberOfBlocksOfType(Block.Type.Plutonium) * 4;
        System.out.println("temp " + temp);
        return temp;
    }

    private int numberOfBlocksOfType(Block.Type type) {
        if (blocksByType.containsKey(type)) {
            return blocksByType.get(type).size();
        }
        return 0;
    }

    public void updateBlockTemperatures() {

        chunks.forEach((loc, chunk) -> {
            chunk.getBlocks().forEach((blockLoc, block) -> {
                block.updateTemperature();
            });
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
        return Math.toIntExact(id);
    }

}
