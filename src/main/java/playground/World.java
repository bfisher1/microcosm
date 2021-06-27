package playground;

import animation.AnimationBuilder;
import animation.Sprite;
import lombok.Getter;
import lombok.Setter;
import machine.ArmActionSequenceBuilder;
import machine.ArmActionType;
import mob.Grape;
import mob.Mob;
import util.Rectangle;
import util.Vector;
import util.*;
import world.PerlinNoise;
import world.block.*;
import world.block.execution.ConstantlyExecutable;
import world.resource.Item;
import world.resource.WorldItem;
import world.resource.assembly.*;
import world.resource.print.*;
import world.resource.raw.IronRubble;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static world.resource.assembly.AssemblyAction.Weld;

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

        int blockWidth = Block.BLOCK_SCREEN_WIDTH;

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

        // WORLD should just have list of items, and then can tell what block those are on top of when executing block
        // whenever item moves, recalculate which block it's on and notify that block

        // block items can still exist, but when moving they should be disconeccted. maybe better not to tho

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

        List<Block> blocksWithItemsOnTopOfThem = new ArrayList<>();

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
                block.draw(worldGraphics, worldCenterScreenLoc);

                if (block.hasSomethingOnTopOf()) {
                    blocksWithItemsOnTopOfThem.add(block); //.stream().filter(blockItem -> !blockItem.isDeleted()).collect(Collectors.toList()));
                }

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
         * Draw items after blocks.
         */
        blocksWithItemsOnTopOfThem.stream().forEach(block -> {
            block.drawItemsOnTopOf(worldGraphics, worldCenterScreenLoc);
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
        createBlockAt(3, -9, 2, Block.Type.Computer);
        createBlockAt(0, -1, 2, Block.Type.Wire);
        TreadmillBlock treadmillBlock = (TreadmillBlock) createBlockAt(0, -2, 2, Block.Type.Treadmill);
        //treadmillBlock.placeItemsOnTopOf(Collections.singletonList(new IronRubble(2)));

        addItemAt(new IronRubble(2), treadmillBlock);

//        treadmillBlock.placeItemsOnTopOf(Collections.singletonList(new IronRubble(3)));

        createBlockAt(0, -3, 2, Block.Type.Treadmill);
        createBlockAt(0, -4, 2, Block.Type.Treadmill);
        createBlockAt(0, -5, 2, Block.Type.Treadmill);
        SmelterBlock smelterBlock = ((SmelterBlock) createBlockAt(0, -6, 2, Block.Type.Smelter));
        createBlockAt(0, -7, 2, Block.Type.Treadmill);
        createBlockAt(1, -7, 2, Block.Type.Treadmill);
//        ((TreadmillBlock) createBlockAt(0, -8, 2, Block.Type.Treadmill)).setOn(true);
        PrinterBlock printerBlock = ((PrinterBlock) createBlockAt(0, -8, 2, Block.Type.Printer));
        createBlockAt(0, -9, 2, Block.Type.Treadmill);
        List<TrayBlock> assemblyTrayBlocks = new ArrayList<>();
        List<ArmBlock> armBlocks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            assemblyTrayBlocks.add((TrayBlock) createBlockAt(i * 3, -11, 2, Block.Type.Tray));
            assemblyTrayBlocks.get(i).setForAssembly(true);
            armBlocks.add((ArmBlock) createBlockAt(i * 3, -10, 2, Block.Type.Arm));
            armBlocks.get(i).scanForTrays();
        }

        createBlockAt(-1, -11, 2, Block.Type.Tray);
//        createBlockAt(1, -11, 2, Block.Type.Tray);
        createBlockAt(-1, -10, 2, Block.Type.Tray);
        createBlockAt(1, -10, 2, Block.Type.Tray);
        createBlockAt(-1, -9, 2, Block.Type.Tray);
        createBlockAt(1, -9, 2, Block.Type.Tray);

        printerBlock.changeResource(PrintableResourceCode.Iron);
        printerBlock.makeRequest(new PrintItemRequest(PrintDesignCode.Drill, PrintableResourceCode.Iron, 1, Size.Medium));
        printerBlock.makeRequest(new PrintItemRequest(PrintDesignCode.Gear, PrintableResourceCode.Iron, 1, Size.Small));
        printerBlock.addFuel(100);
//        treadmillBlock.setOn(true);
        smelterBlock.addFuel(10);



        // weld10Times(2 disc, 2 tire, 2 largeGear, 3 motor, 2 smallGear, 1 panel) => 1 chassis




        List<Map<String, Integer>> inputItemTypeQuantities = new ArrayList<>();
        List<AssemblyRequest> assemblyRequests = new ArrayList<>();

        // ASSEMBLY TRAY 1 - CHASSIS
        new WorldItem(new PrintedItem(PrintedResourceCode.Iron, PrintDesignCode.Panel, Size.Small, 1), this, assemblyTrayBlocks.get(0).getX(), assemblyTrayBlocks.get(0).getY(), assemblyTrayBlocks.get(0).getZ());
        new WorldItem(new PrintedItem(PrintedResourceCode.Iron, PrintDesignCode.Tire, Size.Small, 2), this, assemblyTrayBlocks.get(0).getX(), assemblyTrayBlocks.get(0).getY(), assemblyTrayBlocks.get(0).getZ());
        new WorldItem(new PrintedItem(PrintedResourceCode.Iron, PrintDesignCode.Gear, Size.Small, 2), this, assemblyTrayBlocks.get(0).getX(), assemblyTrayBlocks.get(0).getY(), assemblyTrayBlocks.get(0).getZ());
        new WorldItem(new PrintedItem(PrintedResourceCode.Iron, PrintDesignCode.Disc, Size.Small, 2), this, assemblyTrayBlocks.get(0).getX(), assemblyTrayBlocks.get(0).getY(), assemblyTrayBlocks.get(0).getZ());
//        new WorldItem(new PrintedItem(PrintedResourceCode.Rubber, PrintDesignCode.Gear, Size.Medium, 2), this, assemblyTrayBlocks.get(0).getX(), assemblyTrayBlocks.get(0).getY(), assemblyTrayBlocks.get(0).getZ());
        new WorldItem(new MotorItem(3), this, assemblyTrayBlocks.get(0).getX(), assemblyTrayBlocks.get(0).getY(), assemblyTrayBlocks.get(0).getZ());

        inputItemTypeQuantities.add(new HashMap<>());

        inputItemTypeQuantities.get(0).put("PRINTED_DISC", 2);
        inputItemTypeQuantities.get(0).put("PRINTED_TIRE", 2);
        inputItemTypeQuantities.get(0).put("PRINTED_GEAR", 2);
        inputItemTypeQuantities.get(0).put("MOTOR", 3);
        inputItemTypeQuantities.get(0).put("PRINTED_PANEL", 1);

        List<AssemblyWork> assemblyWork = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            assemblyWork.add(AssemblyWork.builder().assemblyAction(AssemblyAction.Weld).duration(1000).build());
            assemblyWork.add(AssemblyWork.builder().assemblyAction(AssemblyAction.Screw).times(3).build());
        }

        assemblyRequests.add(new AssemblyRequest(AssembledCode.RobotChassis, 1, assemblyWork, inputItemTypeQuantities.get(0)));

        armBlocks.get(0).makeRequest(assemblyRequests.get(0));

        // ASSEMBLY TRAY 2 - FRAME
        new WorldItem(new PrintedItem(PrintedResourceCode.Iron, PrintDesignCode.Panel, Size.Small, 6), this, assemblyTrayBlocks.get(1).getX(), assemblyTrayBlocks.get(1).getY(), assemblyTrayBlocks.get(1).getZ());

        inputItemTypeQuantities.add(new HashMap<>());
        inputItemTypeQuantities.get(1).put("PRINTED_PANEL", 6);

        assemblyRequests.add(new AssemblyRequest(AssembledCode.RobotFrame, 1, assemblyWork, inputItemTypeQuantities.get(1)));

        armBlocks.get(1).makeRequest(assemblyRequests.get(1));

        // ASSEMBLY TRAY 3 - ARM
        new WorldItem(new PrintedItem(PrintedResourceCode.Iron, PrintDesignCode.Stick, Size.Small, 2), this, assemblyTrayBlocks.get(2).getX(), assemblyTrayBlocks.get(2).getY(), assemblyTrayBlocks.get(2).getZ());

        inputItemTypeQuantities.add(new HashMap<>());
        inputItemTypeQuantities.get(2).put("PRINTED_STICK", 2);

        assemblyRequests.add(new AssemblyRequest(AssembledCode.RobotArm, 1, assemblyWork, inputItemTypeQuantities.get(2)));

        armBlocks.get(2).makeRequest(assemblyRequests.get(2));

        // ASSEMBLY TRAY 4 - HEAD
        new WorldItem(new PrintedItem(PrintedResourceCode.Iron, PrintDesignCode.Panel, Size.Small, 6), this, assemblyTrayBlocks.get(3).getX(), assemblyTrayBlocks.get(3).getY(), assemblyTrayBlocks.get(3).getZ());

        inputItemTypeQuantities.add(new HashMap<>());
        inputItemTypeQuantities.get(3).put("PRINTED_PANEL", 6);

        assemblyRequests.add(new AssemblyRequest(AssembledCode.RobotHead, 1, assemblyWork, inputItemTypeQuantities.get(3)));

        armBlocks.get(3).makeRequest(assemblyRequests.get(3));


        /*
        armBlock.beginSequence(
                ArmActionSequenceBuilder.getBuilder()
                        .type(ArmActionType.Extend)
                            .arg("goal", true)
                        .type(ArmActionType.Grab)
//                            .arg()
                        .type(ArmActionType.Face)
                            .arg("rate", .7)
                            .arg("direction", Block.Direction.Right)
//                        .type(ArmActionType.Screw)
//                            .arg("times", 3)
//                        .type(ArmActionType.Weld)
//                            .arg("duration", 1000L)
                        .type(ArmActionType.Release)
                        .type(ArmActionType.Face)
                            .arg("rate", -.7)
                            .arg("direction", Block.Direction.Up)
//                        .type(ArmActionType.Wait)
//                            .arg("duration", 3000L)
                        .type(ArmActionType.Extend)
                            .arg("goal", false)
                        .build()
        );
         */



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

    private void addItemAt(Item item, Block block) {
        addItemAt(item, block.getLocation());
    }

    private void addItemAt(Item item, BlockLocation location) {
        new WorldItem(item, this, location.x, location.y, location.z);
    }

    private void addItemAt(Item item, double x, double y, int z) {
        new WorldItem(item, this, x, y, z);
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

    /**
     * Determine which block a point is at.
     *
     * Each block covers x.0 to x.999
     *                   y.0 to y.999
     */
    public Optional<Block> getBlockAt(double x, double y, double z) {
        BlockLocation location = new BlockLocation((int) x, (int) y, (int) z);
        if (blocks.containsKey(location)) {
            return Optional.of(blocks.get(location));
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

    public Optional<Block> getBlockAt(BlockLocation location) {
        if (blocks.containsKey(location)) {
            return Optional.of(blocks.get(location));
        }
        return Optional.empty();
    }

    public <T> Optional<T> getBlockAt(BlockLocation location, Class<T> clazz) {
        if (blocks.containsKey(location)) {
            try {
                return Optional.of((T) (blocks.get(location)));
            } catch (Exception e) {
                return Optional.empty();
            }
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
