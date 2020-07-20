package world.block;

import world.World;

import javax.persistence.Entity;

@Entity
public class WaterBlock extends Block {
    public WaterBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("water-1.png");
    }
}
