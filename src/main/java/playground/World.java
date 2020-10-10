package playground;

import animation.AnimationBuilder;
import animation.Sprite;
import lombok.Getter;
import lombok.Setter;
import mob.*;
import util.MathUtil;
import util.Rand;
import world.PerlinNoise;
import world.block.Block;
import world.block.BlockFactory;
import world.block.TreadmillBlock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class World {

    private static int worldCount = 0;

    private int id = 0;

    public static List<World> worlds = new ArrayList<>();

    public List<Mob> mobs = new ArrayList<>();

    public void replaceBlockWithType(Block old, Block.Type newType) {
        old.removeFromWorld();
        blocks.put(old.getLocation(), BlockFactory.create(old.getLocation(), newType, this));
    }

    public enum Type {
        World,
        Sun,
        Moon,
        Asteroid
    }

    private Map<BlockLocation, Block> blocks = new ConcurrentHashMap<>();

    private double x, y;

    private int radius;

    //
    private static double WORLD_COORD_SCALE = 32.0;

    public World() {
        worlds.add(this);
        worldCount++;
        this.id = worldCount;
    }

    public World(double x, double y, int radius) {
        this();
        this.radius = radius;
        this.x = x;
        this.y = y;
        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {

                if (MathUtil.dist(0, 0, i, j) <= radius) {
                    createBlockAt(i, j, 0, Block.Type.Stone);
                    double height = PerlinNoise.getHeight(i, j, .04, 8, 1);
                    if (height > .9) {
                        createBlockAt(i, j, 1, Block.Type.Water);
                    } else {
                        createBlockAt(i, j, 1, Block.Type.Grass);
                    }
                    if (Rand.randDouble() < .05) {
                        createBlockAt(i, j, 2, Block.Type.Tree);
                    }
                }
            }

        }


//        int mountainHeight = 5;
//
//        for(int z = 1; z < mountainHeight; z++) {
//            int heightLeft = mountainHeight - z;
//            for(int i = -heightLeft; i < heightLeft; i++ ) {
//                for(int j = -heightLeft; j < heightLeft; j++ ) {
//                    createBlockAt(i, j, z * 10, Block.Type.Stone);
//                }
//            }
//        }

        // getBlockAt(0, 0, 4).get()

        createBlockAt(5, 5, 2, Block.Type.Injector);
        ((TreadmillBlock) createBlockAt(5, 6, 2, Block.Type.Treadmill)).setOn(true);
        ((TreadmillBlock) createBlockAt(5, 4, 2, Block.Type.Treadmill)).setOn(true);
        createBlockAt(5, 7, 2, Block.Type.Water);


        createBlockAt(6, 6, 2, Block.Type.Grass);


        int s = 5;

        for(int i = 0; i < 1; i++) {
            mobs.add(new Imp(Rand.randDouble() * s, Rand.randDouble() * s, 2, this));
            mobs.add(new Wolf(Rand.randDouble() * s, Rand.randDouble() * s, 2, this));
            mobs.add(new Wizard(Rand.randDouble() * s, Rand.randDouble() * s, 2, this));
            mobs.add(new Baboon(Rand.randDouble() * s, Rand.randDouble() * s, 2, this));
            mobs.add(new Moose(Rand.randDouble() * s, Rand.randDouble() * s, 2, this));
        }
        mobs.add(new Grape(8, 8, 2, this));

    }

    protected Block createBlockAt(int x, int y, int z, Block.Type type) {
        BlockLocation location = new BlockLocation(x, y, z);
        Block block = BlockFactory.create(location, type, this);
        blocks.put(location, block);
        blocks.get(location).setZ(z);
        adjustSprite(blocks.get(location));
        return blocks.get(location);
    }

    public Optional<Block> getBlockAt(int x, int y, int z) {
        BlockLocation location = new BlockLocation(x, y, z);
        if (blocks.containsKey(location)) {
            return Optional.of(blocks.get(location));
        }
        return Optional.empty();
    }

    public void adjustSprite(Block block) {
        adjustSprite(block, block.getSprite());
        block.getSprite().backgroundSprites.forEach(sprite -> {
            adjustSprite(block, sprite);
        });
    }

    public void adjustSprite(Mob mob) {
        adjustSprite(mob, mob.getSprite());
        mob.getSprite().backgroundSprites.forEach(sprite -> {
            adjustSprite(mob, sprite);
        });
    }

    private Set<Block> blockMap = new HashSet<>();

    public void adjustSprite(Mob mob, Sprite sprite) {
        int s = 22;
        int s2 = 22;
        int s3 = 10;
        // TODO, work for off world anims

        if (mob instanceof Grape) {
            Optional<Block> block = mob.getBlockBelow();
            if (block.isPresent()) {



                double y = block.get().getY();//
                double x = block.get().getX();//
                double z = mob.getLocation().getZ();
                sprite.setX(x * s + (y) * s2 + (this.x - this.y) * WORLD_COORD_SCALE + Camera.getInstance().getX());
                sprite.setY(x * s - (y) * s2 - z * s3 + (this.x + this.y) * WORLD_COORD_SCALE + Camera.getInstance().getY());
                // we tack on the id to make worlds consistently above/below others
                sprite.setZ(z + ((double) id) / 10000.0 );

                blockMap.add(block.get());
                for (int i = -1; i < 1; i++) {
                    for(int j = -1; j < 1; j++) {
                        Optional<Block> otherBlock = mob.getBlockBelow().get().blockRelative(i, j, 0);
                        if (otherBlock.isPresent()) {
                            otherBlock.get().getSprite().setAnimation(AnimationBuilder.getBuilder().fileName("3d/sand.png").build());
                        }
                    }
                }
                block.get().getSprite().setAnimation(AnimationBuilder.getBuilder().fileName("3d/water-still.png").build());
                System.out.println("Block at: " + block.toString());

            } else {
                //System.out.println("couldnt find block ++++++++++++++++++++++++++++++++++++++++++++++++++++++=====+++=");
            }
        }
    }

    public void adjustSprite(Block block, Sprite sprite) {
        int s = 22;
        int s2 = 22;
        int s3 = 10;
        sprite.setX(block.getX() * s + block.getY() * s2 + (this.x - this.y) * WORLD_COORD_SCALE + Camera.getInstance().getX());
        sprite.setY(block.getX() * s - block.getY() * s2 - block.getZ() * s3 + (this.x + this.y) * WORLD_COORD_SCALE + Camera.getInstance().getY());
        // we tack on the id to make worlds consistently above/below others
        sprite.setZ(block.getZ() + ((double) id) / 10000.0 );
    }

    public void shift(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public List<World> getNearbyWorlds() {
        // toDO filter nearbyness
        // filter out this world
        return worlds;
    }


    public double temperatureOutput() {
        //toDO port
        return blocks.values().stream().filter(block -> block.getType().equals(Block.Type.Sun)).count() * 5 +
               blocks.values().stream().filter(block -> block.getType().equals(Block.Type.Uranium)).count() * 2 +
               blocks.values().stream().filter(block -> block.getType().equals(Block.Type.Plutonium)).count() * 1;
    }


    public Collection<Block> getBlockList() {
        return blocks.values();
    }
}
