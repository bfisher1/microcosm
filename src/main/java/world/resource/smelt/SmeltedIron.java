package world.resource.smelt;

import animation.AnimationBuilder;
import lombok.Getter;
import lombok.Setter;
import world.resource.print.PrintableResourceCode;
import world.resource.print.PrintableResource;

@Setter
@Getter
public class SmeltedIron extends SmeltedItem implements PrintableResource {

    public SmeltedIron(double quantity) {
        setQuantity(quantity);
        setAnimation(AnimationBuilder.getBuilder().fileName("iron-smelted.png").build());
    }

    @Override
    public SmeltedCode getSmeltedCode() {
        return SmeltedCode.Iron;
    }

    @Override
    public PrintableResourceCode getResourceCode() {
        return PrintableResourceCode.Iron;
    }
}
