package mob;

import animation.AnimationBuilder;
import animation.Sprite;
import playground.World;
import playground.WorldLocation;

public class Wolf extends Mob {
    public Wolf(double x, double y, double z, World world) {
        this.setLocation(new WorldLocation(x, y, z, world));
        this.setSprite(new Sprite());
        this.setWalkingLeftAnimation(AnimationBuilder.getBuilder().fileName("wizard_anims/wolf walk left.png").framesAndDelay(8, .2).build());
        this.updateState(State.WalkingLeft);
    }
}
