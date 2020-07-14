package world.block;

import world.World;

import java.util.Random;

public class SunBlock extends Block {
    public SunBlock(int x, int y, World world) {
        super(x, y, world);
        double rand = (new Random()).nextDouble();
        if (rand < .3)
            setAnimName("sunblock.png");
        else if (rand < .5)
            setAnimName("red-starblock.png");
        else if (rand < .7)
            setAnimName("red-starblock.png");
        else
            setAnimName("sunblock.png");
    }
}
