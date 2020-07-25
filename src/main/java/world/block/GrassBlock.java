package world.block;

import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "grass_block")
public class GrassBlock extends Block {
    public GrassBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("grass.png");
    }
}
