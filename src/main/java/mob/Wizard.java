package mob;

import animation.AnimationBuilder;
import animation.Sprite;
import playground.World;
import playground.WorldLocation;

public class Wizard extends Mob {
    public Wizard(double x, double y, double z, World world) {
        this.setLocation(new WorldLocation(x, y, z, world));
        this.setSprite(new Sprite());
        this.setWalkingLeftAnimation(AnimationBuilder.getBuilder().fileName("wizard_anims/wiz left gun still.png").build());
        this.updateState(State.WalkingLeft);
    }
}
