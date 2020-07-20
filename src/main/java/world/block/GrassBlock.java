package world.block;

import world.World;

import javax.persistence.Entity;

@Entity
public class GrassBlock extends Block {
    public GrassBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("grass.png");
    }
}
