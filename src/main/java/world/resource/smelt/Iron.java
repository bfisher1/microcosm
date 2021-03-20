package world.resource.smelt;

import animation.AnimationBuilder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Iron extends SmeltedItem {

    public Iron(double quantity) {
        setQuantity(quantity);
        setAnimation(AnimationBuilder.getBuilder().fileName("iron-smelted.png").build());
    }

    @Override
    public SmeltedCode getSmeltedCode() {
        return SmeltedCode.Iron;
    }
}
