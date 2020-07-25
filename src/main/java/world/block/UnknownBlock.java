package world.block;

import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "unknown_block")
public class UnknownBlock extends Block {
    public UnknownBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("unknown.png");
    }
}
