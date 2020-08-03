package world.block;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microcosm.Animation;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "wire_block")
@NoArgsConstructor
public class WireBlock extends ElectronicDevice {

    private enum Direction {
        Vertical,
        Horizontal
    }

    private Direction direction;

    public WireBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("wire-horizontal.png");
        direction = Direction.Horizontal;
    }

    @Override
    public Animation getOnAnimation() {
        return new Animation("wire-horizontal-on.png");
    }

    @Override
    public Animation getOffAnimation() {
        return new Animation("wire-horizontal.png");
    }

    public void setOn(boolean on) {
        super.setOn(on);
        getNeighbors().forEach(block -> {
            if(block.isElectronicDevice()) {
                ElectronicDevice electronicDevice = (ElectronicDevice) block;
                if(!electronicDevice.isOn())
                    electronicDevice.setOn(true);
            }
        });
    }

}
