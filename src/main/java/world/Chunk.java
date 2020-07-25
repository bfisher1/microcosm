package world;

import lombok.Getter;
import lombok.Setter;
import util.IntLoc;
import util.DbClient;
import world.block.Block;

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
    @Transient
    private World world;

    private static int count = 0;

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
            count++;
            // generate using perlin noise
            blocks = WorldGenerator.generateBlocks(xId * CHUNK_SIZE, yId * CHUNK_SIZE, xId * CHUNK_SIZE + CHUNK_SIZE, yId * CHUNK_SIZE + CHUNK_SIZE, world);

            // throw saving these off to a new thread
//            blocks.forEach((loc, block) -> {
//                //block.addToScreen();
//                //block.move(world.getX(), world.getY());
//                DbClient.save(block);
//            });
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    DbClient.saveList(blocks.values().stream().collect(Collectors.toList()));
                }
            })).start();

        }
    }
}