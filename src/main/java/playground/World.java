package playground;

import animation.AnimationBuilder;
import animation.Sprite;
import lombok.Getter;
import lombok.Setter;
import mob.Grape;
import mob.Mob;
import util.Rectangle;
import util.Vector;
import util.*;
import world.PerlinNoise;
import world.block.Block;
import world.block.BlockFactory;
import world.block.SmelterBlock;
import world.block.TreadmillBlock;
import world.block.execution.ConstantlyExecutable;
import world.resource.BlockItem;
import world.resource.raw.IronRubble;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class World {

    private static int worldCount = 0;

    static BufferedImage worldImage = new BufferedImage(GameApp2.WIDTH, GameApp2.HEIGHT, BufferedImage.TYPE_INT_ARGB);

    private ScreenPlotter screenPlotter = new ScreenPlotter();

    private int id = 0;

    public static List<World> worlds = new ArrayList<>();

    private List<Block> visibleBlocks = new ArrayList<>();

    private Map<IntLoc, Block> highestBlocksPerLoc = new HashMap<>();

    private Block hoveredBlock = null;

    public List<Mob> mobs = new ArrayList<>();
    //private static Animation testAnim = AnimationBuilder.getBuilder().fileName("uranium.png").build();

    private Vector velocity = new Vector(0.1 * Rand.randDouble() * 0.0, 0.01 * 0.0);
    private double rotationSpeed = 1.6 * 0; // * (Rand.randDouble() - .5);

    private Map<BlockLocation, ConstantlyExecutable> executingBlocks = new ConcurrentHashMap();

    public void replaceBlockWithType(Block old, Block.Type newType) {
        old.removeFromWorld();
        blocks.put(old.getLocation(), BlockFactory.create(old.getLocation(), newType, this));
    }

    public void draw(Graphics graphics, ScreenInfo screen, Camera camera) {
        /**
         * Input list of worlds we are drawing
         *
         * For each world:
         *      find screen position for center point of the world
         *      use world size, rotation, and scale info to determine the bounding box of the visible world (top / bottom y and leftmost / rightmost x)
         *      create buffered image for world
         *      draw each block onto the world image
         *      draw mobs and other sprites at respected locations on world
         *      draw world image at world location
         *
         *      a fair bit of this could be parallelized
         */

        int blockWidth = Block.BLOCK_WIDTH - 2;

        double cameraToWorldDist = Math.sqrt(Math.pow(x - Camera.getInstance().getX(), 2) + Math.pow(Camera.getInstance().getY() - y, 2));
        double cameraToWorldAngle = -Math.atan2(y - Camera.getInstance().getY(), x - Camera.getInstance().getX()); // * 180.0 / Math.PI;

        double cameraRotationAdjustedX = cameraToWorldDist * Math.cos(cameraToWorldAngle + Camera.getInstance().getOrientation() * Math.PI / 180.0);
        double cameraRotationAdjustedY = cameraToWorldDist * Math.sin(cameraToWorldAngle + Camera.getInstance().getOrientation() * Math.PI / 180.0);
        if (this instanceof Sun) {
//            System.out.println(" " + cameraToWorldDist + " " + cameraToWorldAngle + " " + Camera.getInstance().getOrientation());
        }

        IntLoc worldCenterScreenLoc = new IntLoc((int) (( - Camera.getInstance().getX()) * blockWidth * Camera.getInstance().getZoom()),
                (int) (( - Camera.getInstance().getY()) * blockWidth * Camera.getInstance().getZoom()));

        //TODO, uncomment and eventually get this working in order to get multiple worlds drawing correctly
//        IntLoc worldCenterScreenLoc = new IntLoc((int) ((cameraRotationAdjustedX - Camera.getInstance().getX()) * blockWidth * Camera.getInstance().getZoom()),
//                (int) ((cameraRotationAdjustedY - Camera.getInstance().getY()) * blockWidth * Camera.getInstance().getZoom()));

        IntLoc worldImageCenter = new IntLoc(worldImage.getWidth()/2, worldImage.getHeight()/2);

        Graphics2D worldGraphics = worldImage.createGraphics();
        worldGraphics.setBackground(new Color(255, 255, 255, 0));
        worldGraphics.clearRect(0, 0, worldImage.getWidth(), worldImage.getHeight());

        // get the furthest coordinates on the world that will need to be drawn
        BoundingBox worldBox = getVisibleWorldBoundingBox(worldCenterScreenLoc.getX(), worldCenterScreenLoc.getY(), screen, camera);


        int worldScreenWidth = (int) ((worldBox.getRightX() - worldBox.getLeftX()) * blockWidth * camera.getZoom());
        int worldScreenHeight = (int) ((worldBox.getTopY() - worldBox.getBottomY()) * blockWidth * camera.getZoom());

        worldGraphics.translate(worldImageCenter.getX() + worldCenterScreenLoc.getX(), worldImageCenter.getY() + worldCenterScreenLoc.getY());
        worldGraphics.rotate(Math.toRadians(orientation - Camera.getInstance().getOrientation()), 0, 0);

        // todo, parallelize
        List<Integer> xRange = worldBox.getXRange();
        List<Integer> yRange = worldBox.getYRange();

        List<IntLoc> coords = new ArrayList<>();
        xRange.stream().forEach(x -> {
            yRange.stream().forEach(y -> {
                coords.add(new IntLoc(x, y));
            });
        });

//        coords.sort(new Comparator<IntLoc>() {
//            @Override
//            public int compare(IntLoc loc1, IntLoc loc2) {
//                int dist1 = loc1.getX() * loc1.getX() + loc1.getY() + loc1.getY();
//                int dist2 = loc2.getX() * loc2.getX() + loc2.getY() + loc2.getY();
//                return dist1 - dist2;
//            }
//        });

        setVisibleBlocks(new ArrayList<>());

        Point mouseLocation = GameApp2.getMouseLocation();
        hoveredBlock = null;

        List<BlockItem> blockItems = new ArrayList<>();

        coords.forEach(coord -> {
            final int x = coord.getX();
            final int y = coord.getY();
            // todo keep track of heights of blocks at different locations maybe

            List<Block> blocksToDraw = new ArrayList<>();

            // start with highest block, but keep adding blocks under it if they haven't been drawn

            // TODO, draw by z, then draw x to y
            highestBlockAtLoc(x, y).ifPresent(highestBlock -> {
                for (int z = highestBlock.getZ(); z >= 0; z--) {
                    Optional<Block> blockOpt = getBlockAt(x, y, z);
                    if (blockOpt.isPresent()) {
                        blocksToDraw.add(0, blockOpt.get());
                        if (blockOpt.get().isFullyCoveringView()) {
                            break;
                        }
                    }
                }
            });

            blocksToDraw.stream().forEach(block -> {
                int blockX = block.getX();
                int blockY = block.getY();
                // todo, draw in block class
                block.getAnimation().draw(worldGraphics,
                        (int) (blockX * blockWidth * camera.getZoom() + 0 * worldCenterScreenLoc.getX()),
                        (int) (blockY * blockWidth * camera.getZoom() + 0 * worldCenterScreenLoc.getY()));

                blockItems.addAll(block.getItemsOnTopOf().stream().filter(blockItem -> !blockItem.isDeleted()).collect(Collectors.toList()));

                if (block.isSelected()) {
                    block.SELECTED_ANIMATION.draw(worldGraphics,
                            (int) (blockX * blockWidth * camera.getZoom() + 0 * worldCenterScreenLoc.getX()),
                            (int) (blockY * blockWidth * camera.getZoom() + 0 * worldCenterScreenLoc.getY()));
                }
                //todo end, need to draw items after everything else

                // calculate center screen location
                block.setScreenLocation(calculateBlockScreenLoc(blockX, blockY, blockWidth, 1.0, 1.0, camera, worldCenterScreenLoc, worldImageCenter));

                // calculate corner points of block
                IntLoc pointA = calculateBlockScreenLoc(blockX, blockY, blockWidth, 0, 0, camera, worldCenterScreenLoc, worldImageCenter);
                IntLoc pointB = calculateBlockScreenLoc(blockX, blockY, blockWidth, 2.0, 0, camera, worldCenterScreenLoc, worldImageCenter);
                IntLoc pointC = calculateBlockScreenLoc(blockX, blockY, blockWidth, 2.0, 2.0, camera, worldCenterScreenLoc, worldImageCenter);
                IntLoc pointD = calculateBlockScreenLoc(blockX, blockY, blockWidth, 0, 2.0, camera, worldCenterScreenLoc, worldImageCenter);

                block.setCorners(new Rectangle(pointA, pointB, pointC, pointD));

                if (mouseLocation != null) {
                    if (block.getCorners().isInside(mouseLocation.x, mouseLocation.y)) {
                        hoveredBlock = block;
                        screenPlotter.plot(block.getScreenLocation().getX(), block.getScreenLocation().getY(), true);
                    }
                }

                visibleBlocks.add(block);
            });
        });


        /**
         * Draw block items after blocks.
         */
        blockItems.stream().forEach(item -> {
            Block block = item.getBlock();

            block.getItemsOnTopOf().stream().filter(blockItem -> !blockItem.isDeleted()).forEach(blockItem -> {
                blockItem.getItem().getAnimation().draw(worldGraphics,
                        (int) ((block.getX() * blockWidth + blockItem.getOffsetPx().getX()) * camera.getZoom() + 0 * worldCenterScreenLoc.getX()),
                        (int) ((block.getY() * blockWidth + blockItem.getOffsetPx().getY()) * camera.getZoom() + 0 * worldCenterScreenLoc.getY()));
            });
            // TODO, this logic should be moved to a callback via a gameapp util function that
            // runs all callbacks inside the gameloop, synchronously
            block.getItemsOnTopOf().stream().filter(blockItem -> blockItem.isDeleted())
                    .collect(Collectors.toList())
                    .stream()
                    .forEach(blockItem -> block.removeItemFromOnTopOf(blockItem));
        });


        worldGraphics.dispose();
        // draw world image to graphics at the end

        graphics.drawImage(worldImage, 0, 0, worldImage.getWidth(), worldImage.getHeight(), null);

//        for(int i = -5; i < 5; i++) {
//            for(int j = -5; j < 5; j++) {
//                getBlockAt(i, j, 0).ifPresent(block -> {
//                    screenPlotter.plot(block.getScreenLocation().getX(), block.getScreenLocation().getY(), true);
//                    block.getCorners().getPoints().stream().forEach(cornerPoint -> {
//                        screenPlotter.plot(cornerPoint.getX(), cornerPoint.getY(), true);
//                    });
//                });
//            }
//        }

        screenPlotter.draw(graphics, screen, camera);
    }

    private IntLoc calculateBlockScreenLoc(int blockX, int blockY, int blockWidth, double relativeBlockMultiplierX, double relativeBlockMultiplierY, Camera camera, IntLoc worldCenterScreenLoc, IntLoc worldImageCenter) {

        int centerBlockToCurrentBlockXDelta = (int) (blockX * blockWidth * camera.getZoom() + 0 * worldCenterScreenLoc.getX())
                + (int) ((Block.BLOCK_WIDTH * relativeBlockMultiplierX) / 2 * camera.getZoom());

        int centerBlockToCurrentBlockYDelta = (int) (blockY * blockWidth * camera.getZoom() + 0 * worldCenterScreenLoc.getY())
                + (int) ((Block.BLOCK_WIDTH * relativeBlockMultiplierY) / 2 * camera.getZoom());

        int centerBlockToCurrentBlockRadius = (int) Math.sqrt(centerBlockToCurrentBlockXDelta * centerBlockToCurrentBlockXDelta + centerBlockToCurrentBlockYDelta * centerBlockToCurrentBlockYDelta);

        double centerBlockToCurrentBlockAngle = Math.atan2(centerBlockToCurrentBlockYDelta, centerBlockToCurrentBlockXDelta) + Math.PI * ((orientation - Camera.getInstance().getOrientation()) / 180.0);

        int screenX = worldImageCenter.getX() + worldCenterScreenLoc.getX() +
                + (int) (centerBlockToCurrentBlockRadius * Math.cos(centerBlockToCurrentBlockAngle));
        int screenY = worldImageCenter.getY() + worldCenterScreenLoc.getY() +
                + (int) (centerBlockToCurrentBlockRadius * Math.sin(centerBlockToCurrentBlockAngle));
        return new IntLoc(screenX, screenY);
    }

    private BoundingBox getVisibleWorldBoundingBox(int screenX, int screenY, ScreenInfo screen, Camera camera) {
        // TODO, actuallly calculate visible portion
        return new BoundingBox(50, -50, -50, 50);
    }

    public void startExecuting(BlockLocation location, ConstantlyExecutable block) {
        executingBlocks.put(location, block);
    }

    public void runExecutableBlocks() {
        executingBlocks.values().stream().forEach(executableBlock -> {
            executableBlock.execute();
        });

        executingBlocks.keySet().stream()
                .filter(loc -> executingBlocks.get(loc).shouldStopExecuting())
                .collect(Collectors.toList())
                .stream()
                .forEach(loc -> executingBlocks.remove(loc));
    }

    public enum Type {
        World,
        Sun,
        Moon,
        Asteroid
    }

    private Map<BlockLocation, Block> blocks = new ConcurrentHashMap<>();

    /**
     * These should line up with block size, i.e. 1 x left is 1 block left.
     */
    private double x, y;

    private int radius;

    private double orientation = Rand.randDouble() * 360 * 0;

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
//                    if (i == 0 && j == 0) {
//                        createBlockAt(i, j, 0, Block.Type.Zinc);     // origin
//                    } else if (i == 0 && j > 0) {
//                        createBlockAt(i, j, 0, Block.Type.Sun);      // positive y
//                    } else if (i == 0 && j < 0) {
//                        createBlockAt(i, j, 0, Block.Type.Wire);     // negative y
//                    } else if (i < 0 && j == 0) {
//                        createBlockAt(i, j, 0, Block.Type.Coal);     // negative x
//                    } else if (i > 0 && j == 0) {
//                        createBlockAt(i, j, 0, Block.Type.Computer); // positive x
//                    }
//                    else {
                        createBlockAt(i, j, 0, Block.Type.Stone);
//                    }

                    double height = PerlinNoise.getHeight(i, j, .04, 8, 1);
                    if (height > .9) {
                        createBlockAt(i, j, 1, Block.Type.Water);
                    } else {
                        createBlockAt(i, j, 1, Block.Type.Sand);
                    }

                    double oreHeight = PerlinNoise.getHeight(i, j, .04, 8, 2);

                    if (oreHeight > .9) {
                        //createBlockAt(i, j, 2, Block.Type.Tree);
                        createBlockAt(i, j, 2, Block.Type.Coal);
                    } else if (oreHeight < .1) {
                        createBlockAt(i, j, 2, Block.Type.Iron);
                    }
                    else if (oreHeight > .4 && oreHeight < .5) {
                        createBlockAt(i, j, 2, Block.Type.Clay);
                    }
//                    else if(Rand.randDouble() < .05) {
//                        createBlockAt(i, j, 2, Block.Type.Iron);
//                    }

                }
            }

        }



        // Base hardwired in
        createBlockAt(0, 0, 2, Block.Type.Computer);
        createBlockAt(0, -1, 2, Block.Type.Wire);
        TreadmillBlock treadmillBlock = (TreadmillBlock) createBlockAt(0, -2, 2, Block.Type.Treadmill);
        treadmillBlock.placeItemsOnTopOf(Collections.singletonList(new IronRubble(2)));
//        treadmillBlock.placeItemsOnTopOf(Collections.singletonList(new IronRubble(3)));

        createBlockAt(0, -3, 2, Block.Type.Treadmill);
        createBlockAt(0, -4, 2, Block.Type.Treadmill);
        createBlockAt(0, -5, 2, Block.Type.Treadmill);
        createBlockAt(0, -6, 2, Block.Type.Treadmill);
        createBlockAt(0, -7, 2, Block.Type.Treadmill);
        createBlockAt(1, -7, 2, Block.Type.Treadmill);
        treadmillBlock.setOn(true);
//        SmelterBlock smelterBlock = ((SmelterBlock) createBlockAt(0, -4, 2, Block.Type.Smelter));
//        smelterBlock.addFuel(10);
//        smelterBlock.addItem(new IronRubble(4.0));
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                smelterBlock.startBurningUntilAllSmelted();
//            }
//        }, 1000L);
//        createBlockAt(0, -5, 2, Block.Type.Treadmill);
//        createBlockAt(2, -8, 2, Block.Type.Tray);
//        createBlockAt(0, -7, 2, Block.Type.Printer);




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

        /*
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
        mobs.add(new Grape(8, 8, 2, this));\

         */

    }

    public Block createBlockAt(int x, int y, int z, Block.Type type) {
        BlockLocation location = new BlockLocation(x, y, z);
        Block block = BlockFactory.create(location, type, this);
        blocks.put(location, block);
        blocks.get(location).setZ(z);
        adjustSprite(blocks.get(location));

        if (!highestBlockAtLoc(x, y).isPresent() || highestBlockAtLoc(x, y).get().getZ() < block.getZ() ) {
            highestBlocksPerLoc.put(new IntLoc(x, y), block);
        }

        return blocks.get(location);
    }

    public Optional<Block> highestBlockAtLoc(int x, int y) {
        return highestBlockAtLoc(new IntLoc(x, y));
    }

    public Optional<Block> highestBlockAtLoc(IntLoc loc) {
        if (highestBlocksPerLoc.containsKey(loc)) {
            return Optional.of(highestBlocksPerLoc.get(loc));
        }
        return Optional.empty();
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
        block.getSprite().foregroundSprites.forEach(sprite -> {
            adjustSprite(block, sprite);
        });
    }

    public void adjustSprite(Mob mob) {
        adjustSprite(mob, mob.getSprite());
        mob.getSprite().backgroundSprites.forEach(sprite -> {
            adjustSprite(mob, sprite);
        });
        mob.getSprite().foregroundSprites.forEach(sprite -> {
            adjustSprite(mob, sprite);
        });
    }

    private Set<Block> blockMap = new HashSet<>();

    public void adjustSprite(Mob mob, Sprite sprite) {
        int s = 22;
        int s2 = 22;
        int s3 = 10;
        // TODO, work for off world anims

//        // TODO, work for off world anims
//        sprite.setX(mob.getLocation().getX() * s + mob.getLocation().getY() * s2 + (this.x - this.y) * WORLD_COORD_SCALE + Camera.getInstance().getX());
//        sprite.setY(mob.getLocation().getX() * s - mob.getLocation().getY() * s2 - mob.getLocation().getZ() * s3 + (this.x + this.y) * WORLD_COORD_SCALE + Camera.getInstance().getY());
//        // we tack on the id to make worlds consistently above/below others
//        sprite.setZ(mob.getLocation().getZ() + ((double) id) / 10000.0 );
//

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
                            otherBlock.get().getSprite().setAnimation(AnimationBuilder.getBuilder().fileName("sand.png").build());
                        }
                    }
                }
                block.get().getSprite().setAnimation(AnimationBuilder.getBuilder().fileName("water.png").build());
                System.out.println("Block at: " + block.toString());

            } else {
                //System.out.println("couldnt find block ++++++++++++++++++++++++++++++++++++++++++++++++++++++=====+++=");
            }
        }
    }

    public void adjustSprite(Block block, Sprite sprite) {
        int s = 22;
        int s2 = 22;
        sprite.setX((block.getX() * s + this.x * WORLD_COORD_SCALE + Camera.getInstance().getX()) * Camera.getInstance().getZoom());
        sprite.setY((-block.getY() * s2 + this.y * WORLD_COORD_SCALE + Camera.getInstance().getY()) * Camera.getInstance().getZoom());
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
