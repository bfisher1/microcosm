package world.block;

import lombok.NoArgsConstructor;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "grass_block")
@NoArgsConstructor
public class GrassBlock extends Block {
    public GrassBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("grass.png");
    }

    public void updateTemperature() {
        super.updateTemperature();

        // todo frozen grass
        if (getTemperature() < 49) {
            setAnimation("grass.png");
        }
        else {
            setAnimation("scorched-grass.png");
        }
    }
}
