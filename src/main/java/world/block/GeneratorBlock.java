package world.block;

import lombok.Getter;
import lombok.Setter;
import microcosm.Animation;
import world.World;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class GeneratorBlock extends ElectronicDevice {

    public GeneratorBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("generator-off.png");
        setOn(false);
    }

    @Override
    public Animation getOnAnimation() {
        return new Animation("generator-on.png");
    }

    @Override
    public Animation getOffAnimation() {
        return new Animation("generator-off.png");
    }

    public void setOn(boolean on) {
        super.setOn(on);
        getNeighbors().forEach(block -> {
            if(Type.Wire.equals(block.getType())) {
                WireBlock wire = (WireBlock) block;
                if(!wire.isOn())
                    wire.setOn(true);
            }
        });
    }

}
