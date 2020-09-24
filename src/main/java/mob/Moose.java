package mob;

import animation.AnimationBuilder;
import animation.Sprite;
import playground.World;
import playground.WorldLocation;

public class Moose extends Mob {
    public Moose(double x, double y, double z, World world) {
        this.setLocation(new WorldLocation(x, y, z, world));
        this.setSprite(new Sprite());
        this.setWalkingLeftAnimation(AnimationBuilder.getBuilder().fileName("wizard_anims/moose walk left.png").framesAndDelay(9, .2).build());
        this.updateState(State.WalkingLeft);
    }
}
