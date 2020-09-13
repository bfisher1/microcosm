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
import playground.Camera;
import playground.World;
import util.IntLoc;
import util.Loc;
import util.MathUtil;

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

//    public void handleCollision(Mob mob) {
//        mob.handleCollision(this);
//    }

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

    @Transient
    private boolean loaded;

    public void setZ(int z) {
        this.sprite.setZ(z);
    }

    public Block(int x, int y, World world) {
        this();
        this.x = x;
        this.y = y;
        this.world = world;
        // add method to destroy block and remove it from list when block is removed, replaced
        // add events that occur every x seconds and update blocks in thread
        updateTemperature();
    }

    public void loadSprite() {
        sprite = new Sprite(animation, (int) (x * BLOCK_WIDTH - Camera.getInstance().getX()), (int) (y * BLOCK_WIDTH - Camera.getInstance().getY()), getZ());
        sprite.setZ(getZ());
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
            List<World> nearbyWorlds = world.getNearbyWorlds();
            nearbyWorlds.forEach(otherWorld -> {
                double additionalTemperature = otherWorld.temperatureOutput();
                double distBetweenWorlds = MathUtil.dist(world.getX() + getX(), world.getY() + getY(), otherWorld.getX(), otherWorld.getY());
                additionalTemperature /= distBetweenWorlds;
                additionalTemperature *= 2.7;
                temperature += additionalTemperature;
            });
        }
    }

    public void setType(Type type) {
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
        this.loadSprite();
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
        this.loadSprite();
    }


    public void setAnimation(String animName, int frames, double delay) {
        //TODO refactor and don't use animDelay or animFrames, redundant
        animation = AnimationBuilder.getBuilder()
                .fileName(animName)
                .framesAndDelay(frames, delay)
                .build();
        this.loadSprite();
    }



    private void hideItems() {
        getItems().forEach(item -> {
            Sprite sprite = item.getItem().getSprite();
            item.getItem().setSprite(null);
            // remove sprite from camera if it's present there
            if (sprite != null && Camera.getInstance().getSprites().contains(sprite)) {
                Camera.getInstance().removeSpriteFromScreen(sprite);
            }
        });
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
            Sprite sprite = new Sprite(itemAnimaton, getSprite().getX(), getSprite().getY() , getZ() + 1);
            item.getItem().setSprite(sprite);
            // add item's sprite to camera's list of sprites
            Camera.getInstance().getSprites().add(sprite);
        });
    }

    public Loc getScreenLoc() {
        return new Loc(sprite.getX(), sprite.getY());
    }

    public IntLoc getIntLoc(){
        return new IntLoc(x, y);
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
