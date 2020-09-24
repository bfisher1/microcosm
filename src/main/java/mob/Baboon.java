package mob;

import animation.AnimationBuilder;
import animation.Sprite;
import playground.World;
import playground.WorldLocation;

public class Baboon extends Mob{
    public Baboon(double x, double y, double z, World world) {
        this.setLocation(new WorldLocation(x, y, z, world));
        this.setSprite(new Sprite());
        this.setWalkingLeftAnimation(AnimationBuilder.getBuilder().fileName("wizard_anims/baboon run left.png").framesAndDelay(7, .2).build());
        this.updateState(Mob.State.WalkingLeft);
    }
}
