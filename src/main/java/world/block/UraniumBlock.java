package world.block;

import lombok.NoArgsConstructor;
import playground.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "uranium_block")
@NoArgsConstructor
public class UraniumBlock extends Block {
    public UraniumBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("uranium.png", 7, .1);
        getAnimation().setSharedKey("uranium");
    }
}
