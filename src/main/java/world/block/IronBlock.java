package world.block;

import world.World;

import javax.persistence.Entity;

@Entity
public class IronBlock extends Block {
    public IronBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("iron.png");
    }
}
