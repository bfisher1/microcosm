package world.block;

import lombok.NoArgsConstructor;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "stone_block")
@NoArgsConstructor
public class StoneBlock extends Block {
    public StoneBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("stone.png");
    }
}
