package world.block;

import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "coal_block")
public class CoalBlock extends Block {
    public CoalBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("coal.png");
    }
}
