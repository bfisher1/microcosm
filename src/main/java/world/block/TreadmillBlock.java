package world.block;

import item.Container;
import item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microcosm.Animation;
import player.Camera;
import util.Loc;
import world.World;

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
public class TreadmillBlock extends ElectronicDevice implements Container {

    @Transient
    private List<Item> items;

    public TreadmillBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation(getOffAnimation());
        items = new ArrayList<>();
    }

    @Override
    public Animation getOnAnimation() {
        return new Animation("treadmill-up.png", 10, .2);
    }

    @Override
    public Animation getOffAnimation() {
        return new Animation("treadmill-up-still.png");
    }

    public List<TreadmillBlock> getAlignedTreadmillBlocks() {
        // TODO, add support for horizontal and vertical treadmill blocks
        return getVerticalNeighbors().stream().filter(block -> Type.Treadmill.equals(block.getType())).map(block -> (TreadmillBlock) block).collect(Collectors.toList());
    }

    public void setOn(boolean on) {
        super.setOn(on);
        getAlignedTreadmillBlocks().forEach(treadmillBlock -> {
            if(!treadmillBlock.isOn()) {
                treadmillBlock.setOn(true);
            }
        });
    }

    public void whileOn() {
        items.forEach(item -> {
            item.move(0, -.5);
        });
    }


    @Override
    public void addToScreen(Camera camera) {
        super.addToScreen(camera);
        getItems().forEach(item -> {
            item.getItem().getAnimation().createEntity(getX() * BLOCK_WIDTH - camera.getX(), getY() * BLOCK_WIDTH - camera.getY(), .5, .5);
            // 1 above the block it's on
            item.getItem().getAnimation().getEntity().setZ(getZ() + 1);
        });
    }

    @Override
    public void removeFromScreen() {
        super.removeFromScreen();
        getItems().forEach(item -> {
            com.almasb.fxgl.entity.Entity entity = item.getItem().getAnimation().getEntity();
            if (entity != null) {
                entity.removeFromWorld();
                item.getItem().getAnimation().setEntity(null);
            }
        });
    }

    @Override
    public void setScreenLoc(Loc loc) {
        super.setScreenLoc(loc);
        getItems().forEach(item -> {
            com.almasb.fxgl.entity.Entity entity = item.getItem().getAnimation().getEntity();
            if (entity != null) {
                entity.setX(loc.getX() + getXSpriteOffset() + item.getLayoutOffset().getX() + item.getLocInContainer().getX());
                entity.setY(loc.getY() + getYSpriteOffset() + item.getLayoutOffset().getY() + item.getLocInContainer().getY());
            }
        });
    }


    @Override
    public void addItem(Item item) {
        getItems().add(item);
    }

    @Override
    public void removeItem(Item item) {
        getItems().remove(item);
    }
}
