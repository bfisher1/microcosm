package world.block;

import world.World;

import javax.persistence.Entity;

@Entity
public class UraniumBlock extends Block {
    public UraniumBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("uranium.png", 7, 1.4);
    }
}
