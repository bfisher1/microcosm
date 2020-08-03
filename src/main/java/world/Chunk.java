package world;

import lombok.Getter;
import lombok.Setter;
import microcosm.BlockSaver;
import util.IntLoc;
import util.DbClient;
import world.block.Block;
import world.block.BlockFactory;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "chunk")
public class Chunk {
    public static int CHUNK_SIZE = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int xId;
    private int yId;
    @Transient
    private Map<IntLoc, Block> blocks;

    @ManyToOne
    @JoinColumn(name = "world_id")
    private World world;

    private static int count = 0;

    public Chunk() {
        //
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Chunk) {
            Chunk otherChunk = (Chunk) other;
            return xId == otherChunk.xId && yId == otherChunk.yId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (xId + "" + yId).hashCode();
    }


    public Chunk(int xId, int yId, World world) {
        this.xId = xId;
        this.yId = yId;
        this.blocks = new HashMap<>();
        this.world = world;
    }

    public void load() {
        Optional<Chunk> dbChunk = DbClient.findAllWhere(Chunk.class, String.format("where world_id = %d and xId = %d and yId = %d", getWorld().getId(), xId, yId)).stream().findFirst();
        if(dbChunk.isPresent()) {
            setId(dbChunk.get().getId());
            // load from persistent memory
            Collection<Block> dbBlocks = DbClient.findAllWhere(Block.class, String.format("where chunk_id = %d", id));
            blocks = new HashMap<>();
            dbBlocks.stream().forEach(dbBlock -> {
                Block block = Block.copy(dbBlock);
                blocks.put(new IntLoc(dbBlock.getX(), dbBlock.getY()), block);
                block.setChunk(this);
            });
            int n = 2;
            n++;
        } else {
            count++;
            DbClient.save(this);
            // generate using world generator
            blocks = WorldGenerator.generateBlocks(xId * CHUNK_SIZE, yId * CHUNK_SIZE, xId * CHUNK_SIZE + CHUNK_SIZE, yId * CHUNK_SIZE + CHUNK_SIZE, world);

            blocks.forEach((loc, block) -> {
                block.setChunk(this);
            });


            if (!blocks.isEmpty()) {
                // IS THIS WOKRING? See other loc in structure generator?
                BlockSaver.add(blocks.values().stream().collect(Collectors.toList()));
//                (new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        DbClient.saveBlocks();
//                    }
//                })).start();
            }

        }
    }
}