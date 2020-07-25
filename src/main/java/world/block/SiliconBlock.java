package world.block;

import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "silicon_block")
public class SiliconBlock extends Block {
    public SiliconBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("silicon.png");
    }
}
