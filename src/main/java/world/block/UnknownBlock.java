package world.block;

import lombok.NoArgsConstructor;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "unknown_block")
@NoArgsConstructor
public class UnknownBlock extends Block {
    public UnknownBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("unknown.png");
    }
}
