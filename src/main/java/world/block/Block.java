package world.block;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import item.Itemable;
import lombok.Getter;
import lombok.Setter;
import microcosm.Animation;
import microcosm.AnimationClip;
import microcosm.Collidable;
import microcosm.Mob;
import player.Camera;
import util.IntLoc;
import util.Loc;
import world.World;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Block implements Collidable, Itemable {

    public static int BLOCK_WIDTH = 32;

    @Override
    public boolean isCollidingWith(Collidable otherMob) {
        return false;
    }

    public void handleCollision(Mob mob) {
        mob.handleCollision(this);
    }

    public enum Type {
        Grass,
        Water,
        Coal,
        Stone,
        Sand,
        Tree,
        Copper,
        Zinc,
        Silicon,
        Nickel,
        Iron,
        Sun,
        Uranium,
        Wire,
        Generator,
        Plutonium,
        Treadmill,
        Unknown
    };

    private int x;
    private int y;
    private int z;

    private int xSpriteOffset;
    private int ySpriteOffset;
    private Type type;
    private  Block above = null;
    private Animation animation;
    private Entity entity;
    private World world;
    private boolean loaded;

    public Block(int x, int y, World world) {
        loaded = true;
        this.x = x;
        this.y = y;
        this.world = world;
        z = 1;
        xSpriteOffset = 0;
        ySpriteOffset = 0;
        // add method to destroy block and remove it from list when block is removed, replaced
        // add events that occur every x seconds and update blocks in thread
    }

    public void setType(Type type) {
        // if the block is being initialized still
        if (this.type == null) {
            // add block to dictionary of block types in chunk
            if (!world.getBlocksByType().containsKey(type)) {
                world.getBlocksByType().put(type, new ArrayList<>());
            }
            world.getBlocksByType().get(type).add(this);
        }
        this.type = type;
    }

    public void stack(Block block) {
        block.setZ(getZ() + 1);
        this.above = block;
    }

    public void setAnimation(String animName) {
        animation = new Animation(animName);
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void updateAnimation() {
        if (!animation.isAnim() && entity != null) {
            removeFromScreen();
            addToScreen(Camera.getInstance());
        }
    }

    public void destroy() {
        // flag to remove from list of blocksByType eventually
        loaded = false;
    }

    public void setAnimation(String animName, int frames, double delay) {
        //TODO refactor and don't use animDelay or animFrames, redundant
        animation = new Animation(animName, frames, delay);
    }


    public void removeFromScreen() {
        entity.removeFromWorld();
        world.removeRenderedBlock(this);
        if(above != null)
            above.removeFromScreen();
    }

    public void addToScreen(Camera camera) {
        entity = animation.createEntity(x * BLOCK_WIDTH - camera.getX(), y * BLOCK_WIDTH - camera.getY());
        entity.setZ(getZ());
        world.addRenderedBlock(this);
        if(above != null)
            above.addToScreen(camera);
    }

    public Loc getScreenLoc() {
        return new Loc(entity.getX(), entity.getY());
    }

    public IntLoc getIntLoc(){
        return new IntLoc(x, y);
    }

    public boolean hasNeighborBlock(int offsetX, int offsetY) {
        if (world.isBlockLoaded(x + offsetX, y + offsetY)) {
            Block block = world.getBlockAt(x + offsetX, y + offsetY);
            while(block != null && block.getZ() != z)
                block = block.getAbove();
            return block != null;
        }
        return false;
    }

    public Block getNeighborBlock(int offsetX, int offsetY) {
        Block block = world.getBlockAt(x + offsetX, y + offsetY);
        while(block != null && block.getZ() != z)
            block = block.getAbove();
        return block;
    }

    public List<Block> getNeighbors() {
        List<Block> neighbors = new ArrayList<>();
        if(hasNeighborBlock(0, -1))
            neighbors.add(getNeighborBlock(0, -1));
        if(hasNeighborBlock(-1, 0))
            neighbors.add(getNeighborBlock(-1, 0));
        if(hasNeighborBlock(1, 0))
            neighbors.add(getNeighborBlock(1, 0));
        if(hasNeighborBlock(0, 1))
            neighbors.add(getNeighborBlock(0, 1));
        return neighbors;
    }

    public boolean isElectronicDevice() {
        return Type.Generator.equals(type) || Type.Wire.equals(type);
    }

    public void setScreenLoc(Loc loc) {
        entity.setX(loc.getX() + xSpriteOffset);
        entity.setY(loc.getY() + ySpriteOffset);
        if(above != null)
            above.setScreenLoc(loc);
    }

    public void move(Loc diff) {
        Loc loc = getScreenLoc();
        loc.increase(diff);
        setScreenLoc(loc);
    }

    public boolean onScreen() {
        return entity != null && entity.getX() > 0 && entity.getY() >  0 && entity.getX() < FXGL.getAppWidth() && entity.getY() < FXGL.getAppHeight();
    }

    public void move(double x, double y) {
        move(new Loc(x, y));
        if(above != null)
            above.move(x, y);
    }

}
