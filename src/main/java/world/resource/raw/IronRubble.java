package world.resource.raw;

import animation.AnimationBuilder;
import lombok.Getter;
import lombok.Setter;
import world.resource.smelt.SmeltableItem;

@Getter
@Setter
public class IronRubble extends SmeltableItem {
    @Override
    public SmeltCode getSmeltCode() {
        return SmeltCode.Iron;
    }

    public IronRubble(double quantity) {
        setQuantity(quantity);
        setAnimation(AnimationBuilder.getBuilder().fileName("iron-rubble.png").build());
    }
}
