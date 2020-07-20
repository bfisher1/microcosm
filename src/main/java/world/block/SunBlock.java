package world.block;

import world.World;

import javax.persistence.Entity;
import java.util.Random;

@Entity
public class SunBlock extends Block {
    public SunBlock(int x, int y, World world) {
        super(x, y, world);
        double rand = (new Random()).nextDouble();
        if (rand < .3)
            setAnimation("sunblock.png");
        else if (rand < .5)
            setAnimation("red-starblock.png");
        else if (rand < .7)
            setAnimation("red-starblock.png");
        else
            setAnimation("sunblock.png");
    }
}
