package world.block;

import item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import animation.Animation;
import animation.AnimationBuilder;
import playground.World;
import util.Loc;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "treadmill_block")
@NoArgsConstructor
public class TreadmillBlock extends ElectronicDevice {

    @Transient
    private int width = Block.BLOCK_WIDTH;

    public TreadmillBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation(getOffAnimation());
    }

    @Override
    public Animation getOnAnimation() {
        return AnimationBuilder.getBuilder().fileName("3d/treadmill-up.png").framesAndDelay(10, .02).build();
    }

    @Override
    public Animation getOffAnimation() {
        return AnimationBuilder.getBuilder().fileName("3d/treadmill-up-still.png").build();
    }

    public List<TreadmillBlock> getAlignedTreadmillBlocks() {
        // TODO, add support for horizontal and vertical treadmill blocks
        return null; //getVerticalNeighbors().stream().filter(block -> Type.Treadmill.equals(block.getType())).map(block -> (TreadmillBlock) block).collect(Collectors.toList());
    }

    public void setOn(boolean on) {
        super.setOn(on);
//        getAlignedTreadmillBlocks().forEach(treadmillBlock -> {
//            if(!treadmillBlock.isOn()) {
//                treadmillBlock.setOn(true);
//            }
//        });
    }

    public void clearRemovedItems() {
        List<Item> remove = new ArrayList<>();
        getItems().forEach(item -> {
            if(item.isMarkedAsRemoved(this)) {
                remove.add(item);
            }
        });
        remove.forEach(item -> {
            getItems().remove(item);
        });
    }

    public void whileOn() {
        clearRemovedItems();
        getItems().forEach(item -> {
            item.move(0, -.5);
            if (outsideContainer(item)) {
                moveToNeighboringBlock(item);
            }
            // System.out.println("--- " + item.getLocInContainer());
            // pop from treadmill and set on other one after some point
        });
    }

    private void moveToNeighboringBlock(Item item) {
//        //System.out.println("++++" + item.getLocInContainer());
//        item.markAsRemoved(this);
//        Block neighbor = null;
//        if (Math.abs(item.getLocInContainer().getX()) > Math.abs(item.getLocInContainer().getY()) ) {
//            // x is greater, so move horizontally
//            if (item.getLocInContainer().getX() > 0) {
//                // positive x
//                neighbor = getNeighborBlock(1, 0);
//            } else {
//                // negative x
//                neighbor = getNeighborBlock(-1, 0);
//            }
//        } else {
//            // y is greater, so move vertically
//            if (item.getLocInContainer().getY() > 0) {
//                // positive y
//                neighbor = getNeighborBlock(0, 1);
//            } else {
//                // negative y
//                neighbor = getNeighborBlock(0, -1);
//            }
//        }
//        item.setLocInContainer(new Loc(0, 0));
//        neighbor.addItem(item);
//        item.move(0,0);
//        //System.out.println(this + " neighbor " + neighbor);
    }

    private boolean outsideContainer(Item item) {
        return Math.abs(item.getLocInContainer().getX()) > width || Math.abs(item.getLocInContainer().getY()) > width;
    }


//    @Override
//    public void addToScreen(Camera camera) {
//        super.addToScreen(camera);
//
//    }
//
//    @Override
//    public void removeFromScreen() {
//        super.removeFromScreen();
//    }

//    @Override
//    public void setScreenLoc(Loc loc) {
//        super.setScreenLoc(loc);
//        getItems().forEach(item -> {
//            com.almasb.fxgl.entity.Entity entity = item.getItem().getAnimation().getEntity();
//            if (entity != null) {
//                entity.setX(loc.getX() + getXSpriteOffset() + item.getLayoutOffset().getX() + item.getLocInContainer().getX());
//                entity.setY(loc.getY() + getYSpriteOffset() + item.getLayoutOffset().getY() + item.getLocInContainer().getY());
//            }
//        });
//    }
}
