package world.block;

import animation.AnimationBuilder;
import lombok.NoArgsConstructor;
import playground.World;
import world.TemperatureState;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Optional;

@Entity
@Table(name = "water_block")
@NoArgsConstructor
public class WaterBlock extends Block {
    // TODO, should evaporate or freeze depending on temperature
    // if evaporates, can stay in world atmosphere
    public WaterBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation(AnimationBuilder.getBuilder().fileName("3d/water-still.png").build());
    }


    public void updateTemperature() {
        super.updateTemperature();
        if (getTemperature() > 100) {
            removeFromWorld();
            // todo, add steam
        }
    }

}
