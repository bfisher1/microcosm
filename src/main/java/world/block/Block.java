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
import playground.BlockLocation;
import playground.Camera;
import playground.World;
import util.*;
import util.Rectangle;
import world.resource.BlockItem;
import world.resource.WorldItem;
import world.resource.assembly.AssembledItem;
import world.resource.assembly.AssemblyRequest;

import javax.persistence.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "block")
public abstract class Block implements Collidable, Itemable, Container {

    public final static int BLOCK_WIDTH = 32;

    public final static int BLOCK_SCREEN_WIDTH = Block.BLOCK_WIDTH - 2;

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

    public boolean onFire = false;

    public void setOnFire(boolean onFire) {
        // if onFire now but wasn't previously, create fire sprite
        if (onFire && !this.onFire) {
            this.getSprite().addBackgroundSprite(AnimationBuilder.getBuilder().fileName("3d/fire.png").framesAndDelay(10, 0.1).build());
        }
        this.onFire = onFire;
    }

    public BlockLocation getLocation() {
        return new BlockLocation(this.x, this.y, this.z);
    }

    public void placeItemsOnTopOf(List<world.resource.Item> items) {
        items.stream().forEach(item -> {
            new WorldItem(item, world, getX(), getY(), getZ());
        });
    }

    public BlockItem placeItemOnTopOf(world.resource.Item item) {
        BlockItem blockItem = new BlockItem(item, this, Rand.randomIntLocCenteredAtZero(5, 5).toLoc());
        itemsOnTopOf.add(blockItem);
        return blockItem;
    }

    public void removeItemFromOnTopOf(world.resource.BlockItem item) {
        itemsOnTopOf.remove(item);
    }

    public void addItemOnTopOf(WorldItem worldItem) {
        itemsOn.put(worldItem.getId(), worldItem);
    }

    public void removeItemOnTopOf(WorldItem worldItem) {
        if (itemsOn.containsKey(worldItem.getId())) {
            itemsOn.remove(worldItem.getId());
        }
    }

    public void draw(Graphics2D graphics, IntLoc worldCenterScreenLoc) {
        getAnimation().draw(graphics,
                (int) (x * Block.BLOCK_SCREEN_WIDTH * Camera.getInstance().getZoom() + 0 * worldCenterScreenLoc.getX()),
                (int) (y * Block.BLOCK_SCREEN_WIDTH * Camera.getInstance().getZoom() + 0 * worldCenterScreenLoc.getY())
        );
    }

    public void drawItemsOnTopOf(Graphics2D graphics, IntLoc worldCenterScreenLoc) {
        this.getItemsOn().values().stream().forEach(worldItem -> {
            worldItem.getItem().getAnimation().draw(graphics,
                    (int) ((worldItem.getLoc().getX() * Block.BLOCK_SCREEN_WIDTH) * Camera.getInstance().getZoom() + 0 * worldCenterScreenLoc.getX()),
                    (int) ((worldItem.getLoc().getY() * Block.BLOCK_SCREEN_WIDTH) * Camera.getInstance().getZoom() + 0 * worldCenterScreenLoc.getY()));
        });
    }

    public boolean hasSomethingOnTopOf() {
        return !itemsOn.isEmpty();
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
        Smelter,
        Tray,
        Printer,
        Clay,
        Arm,
        Unknown
    };

    public enum Direction {
        Up,
        Down,
        Left,
        Right,
        UpRight,
        UpLeft,
        DownRight,
        DownLeft
    }

    private Direction direction = Direction.Up;

    private int x;
    private int y;
    private int z;

    private IntLoc screenLocation;
    private Rectangle corners;

    private List<BlockItem> itemsOnTopOf = new ArrayList<>();

    Map<Long, WorldItem> itemsOn = new HashMap<>();

    /**
     * Extra amount z is incremented by when drawing.
     */
    private int drawZOffset = 0;

    private int temperature;

    private boolean selected;

    private boolean fullyCoveringView = true;

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

    public final static Animation SELECTED_ANIMATION = AnimationBuilder.getBuilder().fileName("select-green.png").build();

    @Transient
    private Sprite sprite;

    @ManyToOne
    @JoinColumn(name = "world_id")
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private World world;

    private Set<BlockLocation> blockReferences = new HashSet<>();
    private Set<BlockLocation> blocksThatReferenceThis = new HashSet<>();

    @Transient
    private boolean loaded;

    public void setDirection(Direction direction) {
        this.direction = direction;

        if (animation == null || direction == null) {
            return;
        }

        switch (direction) {
            case Up:
                this.animation.setAngle(0);
                break;
            case Down:
                this.animation.setAngle(180);
                break;
            case Left:
                this.animation.setAngle(90);
                break;
            case Right:
                this.animation.setAngle(270);
                break;
        }

        this.animation.load();
    }

    public Direction getOppositeDirection() {
        switch (direction) {
            case Up:
                return Direction.Down;
            case Down:
                return Direction.Up;
            case Left:
                return Direction.Right;
            case Right:
                return Direction.Left;
        }
        return null;
    }

    // todo
    public Direction getClockWiseDirection() {
        throw new RuntimeException("TODO, implement this method");
    }

    // todo
    public Direction getCounterClockWiseDirection() {
        throw new RuntimeException("TODO, implement this method");
    }

    public void setZ(int z) {
        this.z = z;
        if (this.sprite != null)
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

    public boolean isHorizontal() {
        return Direction.Left.equals(direction) || Direction.Right.equals(direction);
    }

    public boolean isVertical() {
        return Direction.Up.equals(direction) || Direction.Down.equals(direction);
    }

    public void loadSprite() {
        if (sprite == null) {
            sprite = new Sprite(animation, (int) (x * BLOCK_WIDTH - Camera.getInstance().getX()), (int) (y * BLOCK_WIDTH - Camera.getInstance().getY()), getZ());
            sprite.setZ(getZ());
            //sprite.addForegroundSprite(AnimationBuilder.getBuilder().fileName("3d/shadow.png").build());
        } else {
            sprite.setAnimation(this.animation);
        }
    }

    public void removeFromWorld() {
        Camera.getInstance().removeSpriteFromScreen(this.getSprite());
        world.getBlocks().remove(getLocation());
    }

    /*
     * Block offset by x, y, z from this one.
     */
    public Optional<Block> blockRelative(int x, int y, int z) {
        BlockLocation location = getLocation();
        location.setX(location.getX() + x);
        location.setY(location.getY() + y);
        location.setZ(location.getZ() + z);

        Block block = world.getBlocks().get(location);
        if (block != null) {
            return Optional.of(block);
        }
        return Optional.empty();
    }

    public Optional<Block> blockAbove() {
        return blockRelative(0, 0, 1);
    }

    public Optional<Block> blockBelow() {
        return blockRelative(0, 0, 1);
    }

    public Optional<Block> getNeighbor(Direction direction) {
        switch (direction) {
            case Up:
                return blockRelative(0, -1, 0);
            case UpLeft:
                return blockRelative(-1, -1, 0);
            case UpRight:
                return blockRelative(1, -1, 0);
            case Down:
                return blockRelative(0, 1, 0);
            case DownLeft:
                return blockRelative(-1, 1, 0);
            case DownRight:
                return blockRelative(1, 1, 0);
            case Right:
                return blockRelative(1, 0, 0);
            case Left:
                return blockRelative(-1, 0, 0);
        }
        return Optional.empty();
    }

    public Optional<Block> getHighestNeighborAtOrUnderHeight(Direction direction) {
        for(int z = getZ(); z >= 0; z--) {
            Optional<Block> neighbor = getNeighbor(direction);
            if (neighbor.isPresent()) {
                return neighbor;
            }
        }
        return Optional.empty();
    }

    public List<Block> cardinalNeighbors() {
        List<Optional<Block>> neighborOptionals = new ArrayList<>();
        neighborOptionals.add(blockRelative(1, 0, 0));
        neighborOptionals.add(blockRelative(-1, 0, 0));
        neighborOptionals.add(blockRelative(0, 1, 0));
        neighborOptionals.add(blockRelative(0, -1, 0));
        return neighborOptionals.stream().filter(n -> n.isPresent()).map(n -> n.get()).collect(Collectors.toList());
    }

    public List<Block> verticalNeighbors() {
        List<Optional<Block>> neighborOptionals = new ArrayList<>();
        neighborOptionals.add(blockRelative(0, 1, 0));
        neighborOptionals.add(blockRelative(0, -1, 0));
        return neighborOptionals.stream().filter(n -> n.isPresent()).map(n -> n.get()).collect(Collectors.toList());
    }

    public List<Block> horizontalNeighbors() {
        List<Optional<Block>> neighborOptionals = new ArrayList<>();
        neighborOptionals.add(blockRelative(1, 0, 0));
        neighborOptionals.add(blockRelative(-1, 0, 0));
        return neighborOptionals.stream().filter(n -> n.isPresent()).map(n -> n.get()).collect(Collectors.toList());
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
                // this really should use the nearest block in the sun to calculate

                // TODO, just get this from block map in world
                Optional<Block> otherWorldCenter = otherWorld.getBlockList().stream().filter(block -> block.getX() == 0 && block.y == 0).findFirst();
                // for now, just using sprites to calculate distance
                if (otherWorldCenter.isPresent()) {
                    Sprite otherWorldCenterSprite = otherWorldCenter.get().sprite;
                    if (sprite != null) {
                        double distBetweenWorlds = MathUtil.dist(otherWorldCenterSprite.getX(), otherWorldCenterSprite.getY(), sprite.getX(), sprite.getY());
                        // for now using radius
                        additionalTemperature /= (distBetweenWorlds - otherWorld.getRadius());
                        additionalTemperature *= 30;
                        temperature += additionalTemperature;
                    }
                }
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
        // preserve the angle of the previous animation
        int angle = animation != null ? animation.getAngle() : 0;
        setAnimation(AnimationBuilder.getBuilder().fileName(animName).angle(angle).build());
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
        // reset direction to update the animation accordingly
        this.setDirection(direction);
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
        return "{" + type.toString() + ": (" + getX() + "," + getY() + ", " + getZ() + ")}";
    }

    // todo, this is deprecated
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

    public void addBlockReference(BlockLocation blockLocation) {
        this.blockReferences.add(blockLocation);
        this.world.getBlockAt(blockLocation).get().getBlocksThatReferenceThis().add(this.getLocation());
    }

    /**
     * When this block needs to delete the reference.
     */
    public void removeBlockReference(BlockLocation blockLocation) {
        this.blockReferences.remove(blockLocation);
        this.world.getBlockAt(blockLocation).get().getBlocksThatReferenceThis().remove(this.getLocation());
    }

    /**
     * When the referenced block is deleted, should be overridden.
     */
    public void onReferencedBlockRemoved(BlockLocation blockLocation) {
        this.blockReferences.remove(blockLocation);
    }

    public Direction getDirectionOfOtherBlock(BlockLocation itemLocation) {
        int xDiff = itemLocation.getX() - getLocation().getX();
        int yDiff = itemLocation.getY() - getLocation().getY();

        if (yDiff == 0) {
            if (xDiff == 0) {
                throw new IllegalArgumentException("This is the same block");
            }
            if (xDiff < 0) {
                return Direction.Left;
            } else {
                return Direction.Right;
            }
        }

        if (xDiff == 0) {
            if (yDiff == 0) {
                throw new IllegalArgumentException("This is the same block");
            }
            if (yDiff < 0) {
                return Direction.Up;
            } else {
                return Direction.Down;
            }
        }

        if (yDiff > 0) {
            if (xDiff < 0) {
                return Direction.DownLeft;
            } else {
                return Direction.DownRight;
            }
        } else {
            if (xDiff < 0) {
                return Direction.UpLeft;
            } else {
                return Direction.UpRight;
            }
        }
    }

    public void replaceInputItemsWithAssembledItem(AssemblyRequest assemblyRequest) {
        assemblyRequest.getInputItemTypeQuantities().keySet().stream().forEach(itemType -> {
            for (int i = 0; i < assemblyRequest.getInputItemTypeQuantities().get(itemType); i++) {
                this.getItemsOn().values().stream().filter(item -> item.getItem().getType().equals(itemType)).findAny().ifPresent(item -> {
                    item.delete();
                });
            }
        });
        new WorldItem(new AssembledItem(assemblyRequest.getAssembledCode()), getWorld(), getX(), getY(), getZ());
    }

}
