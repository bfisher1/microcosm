package world.block;

import lombok.NoArgsConstructor;
import playground.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sand_block")
@NoArgsConstructor
public class SandBlock extends Block {
    public SandBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("sand.png");
    }
}
