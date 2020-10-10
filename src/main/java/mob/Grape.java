package mob;

import animation.AnimationBuilder;
import animation.Sprite;
import playground.GameApp;
import playground.World;
import playground.WorldLocation;

public class Grape extends Mob {

    public Grape(double x, double y, double z, World world) {
        this.setLocation(new WorldLocation(x, y, z, world));
        this.setSprite(new Sprite());
        this.setStillLeftAnimation(AnimationBuilder.getBuilder().fileName("wizard_anims/grape still left.png").build());
        this.setWalkingLeftAnimation(AnimationBuilder.getBuilder().fileName("wizard_anims/grape walk left.png").framesAndDelay(6, .2).build());
        this.setWalkingRightAnimation(AnimationBuilder.getBuilder().fileName("3d/green-selected.png").build());
        this.updateState(State.WalkingRight);
        this.velocity *= 2;
    }
}