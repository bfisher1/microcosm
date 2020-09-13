package world.block;

import lombok.NoArgsConstructor;
import playground.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "nickel_block")
@NoArgsConstructor
public class NickelBlock extends Block {
    public NickelBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("nickel.png");
    }
}
