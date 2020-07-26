package world.block;

import lombok.NoArgsConstructor;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "water_block")
@NoArgsConstructor
public class WaterBlock extends Block {
    public WaterBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("water-1.png");
    }
}
