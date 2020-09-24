package mob;

import animation.Animation;
import animation.Sprite;
import lombok.Getter;
import lombok.Setter;
import playground.Location;
import util.LazyTimer;
import util.Rand;
import world.block.Block;

import java.util.Optional;

@Getter
@Setter
public abstract class Mob {

    public enum State {
        WalkingLeft,
        WalkingRight,
        WalkingUp,
        WalkingDown,
        StillLeft,
        StillRight,
        StillUp,
        StillDown
    }

    private State state;
    private boolean canMoveOffWorld = false;

    Animation walkingLeftAnimation;
    Animation walkingRightAnimation;
    Animation walkingUpAnimation;
    Animation walkingDownAnimation;

    Animation stillLeftAnimation;
    Animation stillRightAnimation;
    Animation stillUpAnimation;
    Animation stillDownAnimation;


    Location<Double> location;
    Sprite sprite;
    double direction = 0.0;
    double velocity = 0.05;
    LazyTimer timer = new LazyTimer(20);

    public Optional<Block> getBlockBelow() {
        return getBlockBelow(0, 0);
    }

    public Optional<Block> getBlockBelow(double xOffset, double yOffset) {
        // subtract 1 from z to get block below
        if (location.getWorld().isPresent()) {
            return location.getWorld().get().getBlockAt((int) (location.getX() + xOffset), (int) (location.getY() + yOffset), location.getZ().intValue() - 1);
        }
        return Optional.empty();
    }

    public boolean moveWithVelocityAndDirection() {
        double x = velocity * Math.cos(direction);
        double y = velocity * Math.sin(direction);
        return move(x, y);
    }

    /**
     * Return if movement was possible.
     */
    public boolean move(double x, double y) {
        if (getBlockBelow(x, y).isPresent()) {
            this.getLocation().setX(this.getLocation().getX() + x);
            this.getLocation().setY(this.getLocation().getY() + y);
            return true;
        }
        return false;
    }

    public void updateState(State state) {
        if (!state.equals(this.state)) {
            this.state = state;
            switch (state) {
                case WalkingLeft:
                    this.sprite.setAnimation(walkingLeftAnimation);
                    break;
                case WalkingRight:
                    this.sprite.setAnimation(walkingRightAnimation);
                    break;
                case WalkingUp:
                    this.sprite.setAnimation(walkingUpAnimation);
                    break;
                case WalkingDown:
                    this.sprite.setAnimation(walkingDownAnimation);
                    break;
                case StillLeft:
                    this.sprite.setAnimation(stillLeftAnimation);
                    break;
                case StillRight:
                    this.sprite.setAnimation(stillRightAnimation);
                    break;
                case StillUp:
                    this.sprite.setAnimation(stillUpAnimation);
                    break;
                case StillDown:
                    this.sprite.setAnimation(stillDownAnimation);
                    break;
            }
        }
    }

    public boolean readyToRun() {
        return timer.ready();
    }

    public void runLogic() {
        timer.reset();
        boolean couldMove = this.moveWithVelocityAndDirection();
        if (!couldMove) {
            this.direction += Rand.randDouble();
        }
    }

}
