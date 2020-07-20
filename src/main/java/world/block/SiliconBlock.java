package world.block;

import world.World;

import javax.persistence.Entity;

@Entity
public class SiliconBlock extends Block {
    public SiliconBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("silicon.png");
    }
}
