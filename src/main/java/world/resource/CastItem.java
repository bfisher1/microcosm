package world.resource;

import item.Itemable;
import lombok.*;
import microcosm.Animation;
import world.block.Block;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CastItem implements Itemable {
    Mold mold;
    Block.Type resourceType;
    private Animation animation;

    public CastItem(Mold mold, Block.Type resourceType) {
        this.mold = mold;
        this.resourceType = resourceType;
        this.animation = initializeAnimation();
    }

    public Animation initializeAnimation() {
        if (animation != null) {
            return animation;
        }
        String directory = "resourceItems/";
        String animName = null;
        switch (mold) {
            case Gear:
                animName = "gear.png";
                break;
            case Cap:
                animName ="cap.png";
                break;
            case Pole:
                animName = "pole.png";
                break;
            case Siding:
                animName = "siding.png";
                break;
            case Tire:
                animName = "tire.png";
                break;
            case Disc:
                animName = "disc.png";
                break;
            case SmallBall:
                animName = "smallBall.png";
                break;
            case SmallStick:
                animName = "smallStick.png";
                break;
            case Gripper:
                animName = "gripper.png";
                break;
            case Drill:
                animName = "drill.png";
                break;
        }
        String animDir = directory + animName;
        return new Animation(animDir);
    }

    @Override
    public Object getType() {
        return resourceType;
    }

}
