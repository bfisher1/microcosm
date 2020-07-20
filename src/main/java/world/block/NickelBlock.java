package world.block;

import world.World;

import javax.persistence.Entity;

@Entity
public class NickelBlock extends Block {
    public NickelBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("nickel.png");
    }
}
