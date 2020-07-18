package world.block;

import microcosm.Animation;
import world.World;

public class TreadmillBlock extends ElectronicDevice {
    public TreadmillBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation(getOffAnimation());
    }

    @Override
    public Animation getOnAnimation() {
        return new Animation("treadmill-up.png", 10, .2);
    }

    @Override
    public Animation getOffAnimation() {
        return new Animation("treadmill-up-still.png");
    }
}
