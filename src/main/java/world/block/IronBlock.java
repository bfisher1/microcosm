package world.block;

import lombok.NoArgsConstructor;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "iron_block")
@NoArgsConstructor
public class IronBlock extends Block {
    public IronBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("iron.png");
    }
}
