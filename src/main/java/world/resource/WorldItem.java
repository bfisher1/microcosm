package world.resource;

import lombok.Getter;
import lombok.Setter;
import playground.BlockLocation;
import playground.World;
import util.Loc;
import world.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class WorldItem {

    private static Long WORLD_ITEM_COUNT = 0L;

    private Long id = WORLD_ITEM_COUNT++;
    private Item item;
    private Loc loc;
    private int z;

    private World world;
    private Set<BlockLocation> blocksItemOn = new HashSet<>();

    public WorldItem(Item item, World world, double x, double y, int z) {
        this.item = item;
        this.world = world;
        this.loc = new Loc(x, y);
        this.z = z;
        this.move(0, 0);
    }

    public boolean move(double xDiff, double yDiff) {
        // calculate new loc
        Loc newLoc = loc.plus(xDiff, yDiff);
        return move(newLoc);
    }

    public boolean move(Loc newLoc) {

        // todo, see if new loc is possible (given walls, etc_)
        // if possible, make new move
        loc = newLoc;
        // calculate the block(s) underneath this item
        Set<BlockLocation> blockLocations = calculateBlocksUnderneath();

        // compare with stored blocksItemOn which blocks the item has moved off of, and what new blocks the item is on
        List<BlockLocation> addedLocations = blockLocations.stream().filter(blockLoc -> !blocksItemOn.contains(blockLoc)).collect(Collectors.toList());
        List<BlockLocation> removedLocations = blocksItemOn.stream().filter(blockLoc -> !blockLocations.contains(blockLoc)).collect(Collectors.toList());

        // notify the blocks of ITEM_MOVE_ON_BLOCK, ITEM_MOVE_OFF_BLOCK events
            // blocks either add or delete the item from their Map<id, worldItem>
        addedLocations.stream().forEach(blockLoc -> {
            world.getBlockAt(blockLoc).ifPresent(block -> {
                block.addItemOnTopOf(this);
            });
        });

        removedLocations.stream().forEach(blockLoc -> {
            world.getBlockAt(blockLoc).ifPresent(block -> {
                block.removeItemOnTopOf(this);
            });
        });

        // update blocksItemOn
        blocksItemOn = blockLocations;
        return true; // return if moved
    }

    public boolean move(double velocity, Block.Direction direction) {
        switch (direction) {
            case Up:
                return move(0, -velocity);
            case Down:
                return move(0, velocity);
            case Left:
                return move(-velocity, 0);
            case Right:
                return move(velocity, 0);
        }
        return false;
    }

    public Set<BlockLocation> calculateBlocksUnderneath() {
        Set<BlockLocation> blockLocations = new HashSet<>();

        // look where each corner, center point is
        List<Loc> locs = getCornerLocations();
        locs.add(loc);
        locs.stream().forEach(corner -> {
            world.getBlockAt(corner.getX(), corner.getY(), z).ifPresent(block -> {
                blockLocations.add(block.getLocation());
            });
        });

        // use world to calculate which block this is on
        return blockLocations;
    }

    private List<Loc> getCornerLocations() {
        List<Loc> corners = new ArrayList<>();
        corners.add(new Loc(loc.getX() + item.getSizePx().getX() / 2, loc.getY() - item.getSizePx().getY() / 2));
        corners.add(new Loc(loc.getX() + item.getSizePx().getX() / 2, loc.getY() + item.getSizePx().getY() / 2));
        corners.add(new Loc(loc.getX() - item.getSizePx().getX() / 2, loc.getY() - item.getSizePx().getY() / 2));
        corners.add(new Loc(loc.getX() - item.getSizePx().getX() / 2, loc.getY() + item.getSizePx().getY() / 2));
        return corners;
    }

    public void delete() {
        blocksItemOn.stream().forEach(blockLoc -> {
            world.getBlockAt(blockLoc).ifPresent(block -> {
                block.removeItemOnTopOf(this);
            });
        });
    }
}
