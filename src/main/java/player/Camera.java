package player;

import lombok.Getter;
import microcosm.Mob;
import animation.Animation;
import animation.Sprite;
import util.IntLoc;
import util.Loc;
import world.World;
import world.block.Block;

import java.util.*;

@Getter
public class Camera {
    private int x;
    private int y;

    private Map<World, Set<IntLoc>> renderedBlocks;
    private Set<Mob> renderedMobs;
    private PriorityQueue<Sprite> sprites = new PriorityQueue<>();

    private static Camera instance;

    public static Camera getInstance() {
        if(instance == null)
            instance = new Camera(-60, -60);
        return instance;
    }

    private Camera(int x, int y) {
        this.x = x;
        this.y = y;
        this.renderedBlocks = new HashMap<>();
        this.renderedMobs = new HashSet<>();
    }

    public void move(int x, int y) {
        this.x += x;
        this.y += y;
        updateVisibleBlocks();
    }

    public void setX(int x) {
        this.x = x;
        updateVisibleBlocks();
    }

    public void setY(int y) {
        this.y = y;
        updateVisibleBlocks();
        updateVisibleMobs();
    }

    private void updateVisibleMobs() {
        //
    }

    public void updateVisibleBlocks() {
        try {
            renderedBlocks.forEach((world, locs) -> {
                locs.forEach(loc -> {
                    if (world.isBlockLoaded(loc.getX(), loc.getY())) {
                        Block block = world.getBlockAt(loc.getX(), loc.getY());
                        if (block != null) {
                            Animation animation = block.getAnimation();
                            block.setScreenLoc(new Loc(world.getX() + loc.getX() * block.BLOCK_WIDTH - x, world.getY() + loc.getY() * block.BLOCK_WIDTH - y));
                        }
                    }
                });
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public IntLoc startBlockLoc(World world) {
        int startX = (int) (-world.getX() + getX()) / Block.BLOCK_WIDTH - 2;
        int startY = (int) (-world.getY() + getY()) / Block.BLOCK_WIDTH - 2;
        return new IntLoc(startX, startY);
    }

    public IntLoc endBlockLoc(World world) {
        IntLoc start = startBlockLoc(world);
        int WIDTH = 500;
        int HEIGHT = 500;
        int endX = start.getX() + WIDTH / Block.BLOCK_WIDTH + 2;
        int endY = start.getY() + HEIGHT / Block.BLOCK_WIDTH + 2;
        return new IntLoc(endX, endY);
    }

    public void renderBlocksThatShouldBeOnScreen() {
        // camera x,y = top left of camera
        renderedBlocks.forEach((world, locs) -> {
            IntLoc start = startBlockLoc(world);
            IntLoc end = endBlockLoc(world);


            List<Block> blocksToAdd = new ArrayList<>();
            int numAdded = 0;

            for(int x =  start.getX(); x < end.getX(); x++) {
                for(int y =  start.getY(); y < end.getY(); y++) {
                    IntLoc loc = new IntLoc(x, y);
                    if(!renderedBlocks.get(world).contains(loc)) {
                        if(world.isBlockLoaded(x, y)) {
                            Block block = world.getBlockAt(x, y);
                            if (!block.onScreen()) {
                                block.addToScreen(this);
                                blocksToAdd.add(block);
                                numAdded++;
                            }
                        }
                    }
                }
            }
            //System.out.println("Added " + numAdded);
        });
    }

    public void addRenderedBlock(Block block) {
        if (!renderedBlocks.containsKey(block.getWorld()))
            renderedBlocks.put(block.getWorld(), new HashSet<>());
        renderedBlocks.get(block.getWorld()).add(block.getIntLoc());
        sprites.add(block.getSprite());
    }

    public void removeRenderedBlock(Block block) {
        renderedBlocks.get(block.getWorld()).remove(block.getIntLoc());
        sprites.remove(block.getSprite());
    }

    public void addWorldToRender(World world) {
        renderedBlocks.put(world, new HashSet<>());
    }

    public void addRenderedMob(Mob mob) {
        renderedMobs.add(mob);
    }

    public void removeRenderedMob(Mob mob) {
        renderedBlocks.remove(mob);
    }

    public void removeBlocksThatShouldNotBeOnScreen() {
        List<Block> blocksToRemove= new ArrayList<>();
        getRenderedBlocks().forEach((world, locs) -> {
            locs.forEach(loc -> {
                if(world.isBlockLoaded(loc.getX(), loc.getY())) {
                    Block block = world.getBlockAt(loc.getX(), loc.getY());
                    if (!block.onScreen()) {
                        blocksToRemove.add(block);
                    }
                }
            });
        });
        blocksToRemove.forEach(block -> {
            block.removeFromScreen();
        });
    }
}
