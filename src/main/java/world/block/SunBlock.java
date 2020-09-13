package world.block;

import lombok.NoArgsConstructor;
import playground.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sun_block")
@NoArgsConstructor
public class SunBlock extends Block {
    public SunBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("3d/sunblock.png");
//        double rand = (new Random()).nextDouble();
//        if (rand < .3)
//            setAnimation("sunblock.png");
//        else if (rand < .5)
//            setAnimation("red-starblock.png");
//        else if (rand < .7)
//            setAnimation("red-starblock.png");
//        else
//            setAnimation("sunblock.png");
    }
}
