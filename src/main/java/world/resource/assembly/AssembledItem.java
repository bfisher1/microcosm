package world.resource.assembly;

import animation.AnimationBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import world.resource.Item;

@Getter
@Setter
@NoArgsConstructor
public class AssembledItem extends Item {
    private AssembledCode assembledCode;

    public AssembledItem(AssembledCode assembledCode) {
        this.assembledCode = assembledCode;
        resetAnimation();
    }

    @Override
    public String getType() {
        return assembledCode.toString().toUpperCase();
    }

    private void resetAnimation() {
        switch (assembledCode) {
            case RobotChassis:
                setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/assembly/chassis.png").scale(.5).build());
                break;
            case RobotArm:
                setAnimation(AnimationBuilder.getBuilder().fileName("arm/arm-still-closed.png").scale(.5).build());
                break;
            case RobotFrame:
                setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/assembly/frame.png").scale(.5).build());
                break;
            case RobotHead:
                setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/assembly/head.png").scale(.5).build());
                break;
        }
    }
}
