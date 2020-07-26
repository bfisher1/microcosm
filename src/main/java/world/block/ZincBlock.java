package world.block;

import lombok.NoArgsConstructor;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "zinc_block")
@NoArgsConstructor
public class ZincBlock extends Block {
    public ZincBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("zinc.png");
    }
}
