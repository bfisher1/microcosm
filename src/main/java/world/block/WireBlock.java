package world.block;

import lombok.Getter;
import lombok.Setter;
import microcosm.Animation;
import world.World;

@Getter
@Setter
public class WireBlock extends ElectronicDevice {

    private enum Direction {
        Vertical,
        Horizontal
    }

    private Direction direction;

    public WireBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("wire-horizontal.png");
        direction = Direction.Horizontal;
    }

    @Override
    public Animation getOnAnimation() {
        return new Animation("wire-horizontal-on.png");
    }

    @Override
    public Animation getOffAnimation() {
        return new Animation("wire-horizontal.png");
    }

}
