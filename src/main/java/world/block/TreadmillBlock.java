package world.block;

import item.Container;
import item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microcosm.Animation;
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

}
