package world.resource.assembly;

import animation.AnimationBuilder;
import world.resource.Item;

public class MotorItem extends Item {
    public MotorItem(int quantity) {
        this.setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/assembly/motor.png").build());
        this.setQuantity((int) quantity);
    }

    @Override
    public String getType() {
        return "MOTOR";
    }
}
