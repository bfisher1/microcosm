package mob;

import animation.AnimationBuilder;
import animation.Sprite;
import playground.World;
import playground.WorldLocation;

public class Imp extends Mob {

    public Imp(double x, double y, double z, World world) {
        this.setLocation(new WorldLocation(x, y, z, world));
        this.setSprite(new Sprite());
        this.setWalkingLeftAnimation(AnimationBuilder.getBuilder().fileName("wizard_anims/imp run left.png").framesAndDelay(5, .2).build());
        this.updateState(State.WalkingLeft);
    }
}

