package world.block;

import com.almasb.fxgl.dsl.FXGL;
import item.Container;
import item.Item;
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
import world.Chunk;
import world.World;

import java.io.Serializable;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "block")
public abstract class Block implements Collidable, Itemable, Container {

    public static int BLOCK_WIDTH = 32;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public static Block copy(Block dbBlock) {
        Block block = BlockFactory.create(dbBlock.getX(), dbBlock.getY(), dbBlock.getType(), dbBlock.getWorld());
        block.setId(dbBlock.getId());
        block.setZ(dbBlock.getZ());
        if (dbBlock.getAbove() != null)
            block.setAbove(Block.copy(dbBlock.getAbove()));
        return block;
    }

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
        Injector,
        Plutonium,
        Treadmill,
        Unknown
    };

    private int x;
    private int y;
    private int z;

    private int xSpriteOffset;
    private int ySpriteOffset;
    @Transient
    private boolean displayItems;

    // just for debugging purposes in DB, couldn't get EnumType.STRING working :/
    private String typeName;

    @Enumerated(EnumType.ORDINAL)
    private Type type;

    @Transient
    private List<Item> items = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "above_id")
    private  Block above = null;
    @Transient
    private Animation animation;
    @Transient
    private com.almasb.fxgl.entity.Entity entity;

    @ManyToOne
    @JoinColumn(name = "world_id")
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private World world;

    @ManyToOne
    @JoinColumn(name = "chunk_id")
    private Chunk chunk;

    @Transient
    private boolean loaded;

    public Block(int x, int y, World world) {
        this();
        this.x = x;
        this.y = y;
        this.world = world;
        // add method to destroy block and remove it from list when block is removed, replaced
        // add events that occur every x seconds and update blocks in thread
    }

    public Block() {
        loaded = true;
        z = 1;
        xSpriteOffset = 0;
        ySpriteOffset = 0;
        displayItems = true;
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
        this.typeName = type.toString();
    }

    public void stack(Block block) {
        // if there is a block above this, try to stack the new block above that one
        if (this.above == null) {
            block.setZ(getZ() + 1);
            this.above = block;
        }
        else {
            this.above.stack(block);
        }
    }

    public void setAnimation(String animName) {
        animation = new Animation(animName);
//        int b = 6;
//        double scale = Math.abs(-getZ() + 1 + b) / b;
//        scale = .95;
//        animation.setScaleX(scale);
//        animation.setScaleY(scale);
//        animation.setBackGround("black.png");
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void updateAnimation() {
        if (entity != null) {
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
        if (entity == null) {
            return;
        }
        entity.removeFromWorld();
        world.removeRenderedBlock(this);
        if(above != null)
            above.removeFromScreen();
        if (displayItems) {
            hideItems();
        }
    }

    private void hideItems() {
        getItems().forEach(item -> {
            com.almasb.fxgl.entity.Entity entity = item.getItem().getAnimation().getEntity();
            if (entity != null) {
                entity.removeFromWorld();
                item.getItem().getAnimation().setEntity(null);
            }
        });
    }

    public void addToScreen(Camera camera) {
        entity = animation.createEntity(x * BLOCK_WIDTH - camera.getX(), y * BLOCK_WIDTH - camera.getY());
        entity.setZ(getZ());
        world.addRenderedBlock(this);
        if(above != null)
            above.addToScreen(camera);
        if (this instanceof InjectorBlock) {
            System.out.println("---------++++++++++++++++++++++-" + displayItems);
        }
        if (displayItems) {
            showItems();
        }
    }

    private void showItems() {
        Camera camera = Camera.getInstance();
        getItems().forEach(item -> {
            item.getItem().getAnimation().createEntity(getX() * BLOCK_WIDTH - camera.getX(), getY() * BLOCK_WIDTH - camera.getY(), .5, .5);
            // 1 above the block it's on
            item.getItem().getAnimation().getEntity().setZ(getZ() + 1);
        });
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
        return getNeighborBlock(offsetX, offsetY, false);
    }

    public Block getNeighborBlock(int offsetX, int offsetY, boolean highest) {
        Block block = world.getBlockAt(x + offsetX, y + offsetY);
        //get block at same z
        while(block != null && block.getZ() != z)
            block = block.getAbove();
        if (block == null) {
            block = world.getBlockAt(x + offsetX, y + offsetY);
            if (highest) {
                while(block != null && block.getAbove() != null)
                    block = block.getAbove();
            }
        }
        return block;
    }


    public List<Block> getHorizontalNeighbors() {
        List<Block> neighbors = new ArrayList<>();
        if(hasNeighborBlock(-1, 0))
            neighbors.add(getNeighborBlock(-1, 0, true));
        if(hasNeighborBlock(1, 0))
            neighbors.add(getNeighborBlock(1, 0, true));
        return neighbors;
    }

    public List<Block> getVerticalNeighbors() {
        List<Block> neighbors = new ArrayList<>();
        if(hasNeighborBlock(0, -1))
            neighbors.add(getNeighborBlock(0, -1, true));
        if(hasNeighborBlock(0, 1))
            neighbors.add(getNeighborBlock(0, 1, true));
        return neighbors;
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
        return Type.Generator.equals(type) || Type.Wire.equals(type) || Type.Treadmill.equals(type);
    }

    public Block top() {
        Block block = this;
        while (block.getAbove() != null) {
            block = block.getAbove();
        }
        return block;
    }

    public void setScreenLoc(Loc loc) {
        entity.setX(loc.getX() + xSpriteOffset);
        entity.setY(loc.getY() + ySpriteOffset);
        if(above != null)
            above.setScreenLoc(loc);
        //System.out.println("setting screen loc");
        getItems().forEach(item -> {
            com.almasb.fxgl.entity.Entity entity = item.getItem().getAnimation().getEntity();
            if (entity != null) {
                entity.setX(loc.getX() + item.getLayoutOffset().getX() + item.getLocInContainer().getX());
                entity.setY(loc.getY() + item.getLayoutOffset().getY() + item.getLocInContainer().getY());
            }
        });
    }

    public String toString() {
        return "{" + type.toString() + ": (" + getX() + "," + getY() + ")}";
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

    public void addItem(Item item) {
        getItems().add(item);
        if (!displayItems) {
            hideItems();
        }
    }

    public void removeItem(Item item) {
        getItems().remove(item);
        if (displayItems) {
            showItems();
        }
    }

}
