package playground;

import lombok.Getter;
import lombok.Setter;
import util.MathUtil;
import world.block.Block;
import world.block.BlockFactory;
import world.block.TreadmillBlock;

import java.util.*;

@Getter
@Setter
public class World {

    public enum Type {
        World,
        Sun,
        Moon,
        Asteroid
    }

    private Map<BlockLocation, Block> blocks = new HashMap<>();

    private double x, y;

    private int radius;

    //
    private static double WORLD_COORD_SCALE = 32.0;

    public World() {
    }

    public World(double x, double y, int radius) {
        this.radius = radius;
        this.x = x;
        this.y = y;
        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                if (MathUtil.dist(0, 0, i, j) <= radius) {
                    createBlockAt(i, j, 0, Block.Type.Grass);
                }
            }

        }
        for(int z = 1; z < 5; z++) {
            createBlockAt(0, 0, z, Block.Type.Stone);
        }

        createBlockAt(5, 5, 1, Block.Type.Injector);
        ((TreadmillBlock) createBlockAt(5, 6, 1, Block.Type.Treadmill)).setOn(true);
        ((TreadmillBlock) createBlockAt(5, 4, 1, Block.Type.Treadmill)).setOn(true);
        createBlockAt(5, 7, 1, Block.Type.Water);


        createBlockAt(6, 6, 1, Block.Type.Grass);

    }

    protected Block createBlockAt(int x, int y, int z, Block.Type type) {
        BlockLocation location = new BlockLocation(x, y, z);
        blocks.put(location, BlockFactory.create(x, y, type, null));
        blocks.get(location).setZ(z);
        adjustSprite(blocks.get(location));
        return blocks.get(location);
    }

    public void adjustSprite(Block block) {
        int s = 22;
        int s2 = 22;
        int s3 = 10;
        block.getSprite().setX(block.getX() * s + block.getY() * s2 + (this.x - this.y) * WORLD_COORD_SCALE + Camera.getInstance().getX());
        block.getSprite().setY(block.getX() * s - block.getY() * s2 - block.getZ() * s3 + (this.x + this.y) * WORLD_COORD_SCALE + Camera.getInstance().getY());
    }

    public void shift(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public List<World> getNearbyWorlds() {
        // toDO port
        return new ArrayList<>();
    }


    public double temperatureOutput() {
        //toDO port
        return 0;
    }


    public Collection<Block> getBlocks() {
        return blocks.values();
    }
}
