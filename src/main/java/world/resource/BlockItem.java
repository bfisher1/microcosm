package world.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import util.Loc;
import world.block.Block;

import java.util.Collections;
import java.util.Optional;

/**
 * Item on a block.
 */
@Getter
@Setter
public class BlockItem {
    private Item item;
    private Block block;
    private Loc offsetPx = new Loc(0, 0);
    private boolean deleted = false;

    public BlockItem(Item item, Block block, Loc offsetPx) {
        this.item = item;
        this.block = block;
        this.offsetPx = offsetPx;
    }

    /**
     * Moves block in direction at speed.
     * @param velocity
     * @param direction
     */
    public boolean move(double velocity, Block.Direction direction) {

        double deltaX = 0;
        double deltaY = 0;

        if (offsetPx.getX() >= Block.BLOCK_WIDTH / 2) {
            deltaX = Block.BLOCK_WIDTH / 2;
        }
        if (offsetPx.getX() <= -Block.BLOCK_WIDTH / 2) {
            deltaX = -Block.BLOCK_WIDTH / 2;
        }
        if (offsetPx.getY() >= Block.BLOCK_WIDTH / 2) {
            deltaY = Block.BLOCK_WIDTH / 2;
        }
        if (offsetPx.getY() <= -Block.BLOCK_WIDTH / 2) {
            deltaY = -Block.BLOCK_WIDTH / 2;
        }
        deltaX *= 1.5;
        deltaY *= 1.5;

        if (Math.abs(deltaX) > 0 || Math.abs(deltaY) > 0) {
            Optional<Block> neighbor = block.getHighestNeighborAtOrUnderHeight(direction);
            if (neighbor.isPresent()) {
                System.out.println("Changing from block " + block.toString());
                System.out.println("   to neighborBlock " + neighbor.get().toString());
                System.out.println("        locPx was " + offsetPx.toString() + ", increasing by " + (new Loc(deltaX, deltaY)));
                this.block = neighbor.get();
                this.deleted = true;
                BlockItem newBlockItem = this.block.placeItemOnTopOf(this.getItem());
                newBlockItem.setOffsetPx(getOffsetPx());
                newBlockItem.getOffsetPx().increase(-deltaX, -deltaY);

                System.out.println("        locPx now " + offsetPx.toString());
            } else {
                return false;
            }
        }

        switch (direction) {
            case Up:
                offsetPx.increase(0, -velocity);
                break;
            case Down:
                offsetPx.increase(0, velocity);
                break;
            case Left:
                offsetPx.increase(-velocity, 0);
                break;
            case Right:
                offsetPx.increase(velocity, 0);
                break;
        }

        return true;
    }
}
