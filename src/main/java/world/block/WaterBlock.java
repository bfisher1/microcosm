package world.block;

import animation.AnimationBuilder;
import lombok.NoArgsConstructor;
import playground.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "water_block")
@NoArgsConstructor
public class WaterBlock extends Block {
    public WaterBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation(AnimationBuilder.getBuilder().fileName("3d/water-still.png").build());
    }
}
