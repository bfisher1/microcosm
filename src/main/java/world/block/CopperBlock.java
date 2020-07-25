package world.block;

import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "copper_block")
public class CopperBlock extends Block {
    public CopperBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("copper.png");
    }
}
