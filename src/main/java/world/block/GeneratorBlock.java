package world.block;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import animation.Animation;
import animation.AnimationBuilder;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "generator_block")
public class GeneratorBlock extends ElectronicDevice {

    public GeneratorBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("generator-off.png");
        setOn(false);
    }

    @Override
    public Animation getOnAnimation() {
        return AnimationBuilder.getBuilder().fileName("generator-on.png").build();
    }

    @Override
    public Animation getOffAnimation() {
        return AnimationBuilder.getBuilder().fileName("generator-off.png").build();
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
