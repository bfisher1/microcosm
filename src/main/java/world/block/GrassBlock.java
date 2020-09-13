package world.block;

import lombok.NoArgsConstructor;
import playground.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "grass_block")
@NoArgsConstructor
public class GrassBlock extends Block {
    public GrassBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("3d/grass.png");
        //updateTemperature();
    }

    public void updateTemperature() {
        super.updateTemperature();
        System.out.println("temperature " + getTemperature());

        if (getTemperature() < 45) {
            setAnimation("3d/frozen-grass.png");
        }
        else if (getTemperature() < 49) {
            setAnimation("3d/grass.png");
        }
        else {
            setAnimation("3d/scorched-grass.png");
        }
    }
}
