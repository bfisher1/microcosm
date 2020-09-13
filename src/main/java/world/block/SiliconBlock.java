package world.block;

import lombok.NoArgsConstructor;
import playground.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "silicon_block")
@NoArgsConstructor
public class SiliconBlock extends Block {
    public SiliconBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("silicon.png");
    }
}
