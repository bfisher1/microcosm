package world.block;

import world.World;

import javax.persistence.Entity;

@Entity
public class ZincBlock extends Block {
    public ZincBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("zinc.png");
    }
}
