package world.block;

import animation.Animation;
import animation.AnimationBuilder;
import animation.Sprite;
import item.Container;
import item.Item;
import item.Itemable;
import lombok.Getter;
import lombok.Setter;
import microcosm.Collidable;
import microcosm.Mob;
import player.Camera;
import util.IntLoc;
import util.Loc;
import util.MathUtil;
import world.Chunk;
import world.World;

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
        Computer,
        Unknown
    };

    private int x;
    private int y;
    private int z;

    private int temperature;

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
    private Sprite sprite;

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
        updateTemperature();
    }

    public Block() {
        loaded = true;
        z = 1;
        xSpriteOffset = 0;
        ySpriteOffset = 0;
        displayItems = true;
    }

    public void updateTemperature() {
        if (world != null) {
            temperature = 0;
            world.getNearbyWorlds().forEach(otherWorld -> {
                double additionalTemperature = otherWorld.temperatureOutput();
                double distBetweenWorlds = MathUtil.dist(world.getX() + getX() * BLOCK_WIDTH, world.getY() + getY() * BLOCK_WIDTH, otherWorld.getX(), otherWorld.getY());
                additionalTemperature /= distBetweenWorlds;
                additionalTemperature *= 8;
                temperature += additionalTemperature;
            });
        }
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
        animation = AnimationBuilder.getBuilder().fileName(animName).build();
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void updateAnimation() {
        if (sprite != null) {
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
        animation = AnimationBuilder.getBuilder()
                .fileName(animName)
                .framesAndDelay(frames, delay)
                .build();
    }


    public void removeFromScreen() {
        if (sprite == null) {
            return;
        }
        Camera.getInstance().getSprites().remove(sprite);
        // entity.removeFromWorld();
        world.removeRenderedBlock(this);
        if(above != null)
            above.removeFromScreen();
        if (displayItems) {
            hideItems();
        }
    }

    private void hideItems() {
        getItems().forEach(item -> {
            Sprite sprite = item.getItem().getSprite();
            item.getItem().setSprite(null);
            // remove sprite from camera if it's present there
            if (sprite != null && Camera.getInstance().getSprites().contains(sprite)) {
                Camera.getInstance().getSprites().remove(sprite);
            }
        });
    }

    public void addToScreen(Camera camera) {
        sprite = new Sprite(animation, x * BLOCK_WIDTH - camera.getX(), y * BLOCK_WIDTH - camera.getY(), 1);
        sprite.setZ(getZ());
        world.addRenderedBlock(this);
        if(above != null)
            above.addToScreen(camera);
        if (this instanceof InjectorBlock) {
            //System.out.println("---------++++++++++++++++++++++-" + displayItems);
        }
        if (displayItems) {
            showItems();
        }
    }

    public void showItems() {
        Camera camera = Camera.getInstance();
        getItems().forEach(item -> {
            Animation itemAnimaton = AnimationBuilder.getBuilder()
                    .animation(item.getItem().getAnimation())
                    .scaleX(.5)
                    .scaleY(.5)
                    .build();

            // 1 above the block it's on, also an extra little to distinguish different items
            Sprite sprite = new Sprite(itemAnimaton, getX() * BLOCK_WIDTH - camera.getX(), getY() * BLOCK_WIDTH - camera.getY() , getZ() + 1);
            item.getItem().setSprite(sprite);
            // add item's sprite to camera's list of sprites
            Camera.getInstance().getSprites().add(sprite);
        });
    }

    public Loc getScreenLoc() {
        try {
            return new Loc(sprite.getX(), sprite.getY());
        } catch(NullPointerException e) {
            throw e;
        }
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
        sprite.setX((int) loc.getX() + xSpriteOffset);
        sprite.setY((int) loc.getY() + ySpriteOffset);
        if(above != null)
            above.setScreenLoc(loc);
        //System.out.println("setting screen loc");
        getItems().forEach(item -> {
            Sprite sprite = item.getItem().getSprite();
            if (sprite != null) {
                sprite.setX((int) (loc.getX() + item.getLayoutOffset().getX() + item.getLocInContainer().getX()));
                sprite.setY((int) (loc.getY() + item.getLayoutOffset().getY() + item.getLocInContainer().getY()));
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
        int WIDTH = 500;
        int HEIGHT = 500;
        return sprite != null && sprite.getX() > 0 && sprite.getY() >  0 && sprite.getX() < WIDTH && sprite.getY() < HEIGHT;
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
