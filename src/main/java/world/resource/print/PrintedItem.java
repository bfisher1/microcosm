package world.resource.print;

import animation.AnimationBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import world.resource.Item;

@Getter
@Setter
@NoArgsConstructor
public class PrintedItem extends Item {
    private PrintedResourceCode resourceCode;
    private PrintDesignCode designCode;
    private Size size;

    public PrintedItem(PrintedResourceCode resourceCode, PrintDesignCode designCode, Size size, int quantity) {
        setDesignCode(designCode);
        setResourceCode(resourceCode);
        setSize(size);
        setQuantity(quantity);
    }

    public void setDesignCode(PrintDesignCode designCode) {
        this.designCode = designCode;
        resetAnimation();
    }

    public void setResourceCode(PrintedResourceCode resourceCode) {
        this.resourceCode = resourceCode;
        resetAnimation();
    }

    public void setSize(Size size) {
        this.size = size;
        resetAnimation();
    }

    private void resetAnimation() {
        if (resourceCode != null && designCode != null && size != null) {
            double scale = 1;

            switch(size) {
                case Small:
                    scale = .3;
                    break;
                case Large:
                    scale = 2;
                    break;
            }

            switch (designCode) {
                case Gear:
                    setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/gear.png").scaleX(scale).scaleY(scale).build());
                    break;
                case Drill:
                    setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/drill.png").scaleX(scale).scaleY(scale).build());
                    break;
                case Panel:
                    setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/siding.png").scaleX(scale).scaleY(scale).build());
                    break;
                case Disc:
                    setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/disc.png").scaleX(scale).scaleY(scale).build());
                    break;
                case Tire:
                    setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/tire.png").scaleX(scale).scaleY(scale).build());
                    break;
                case Stick:
                    setAnimation(AnimationBuilder.getBuilder().fileName("resourceItems/smallStick.png").scaleX(scale).scaleY(scale).build());
                    break;
                default:
                    throw new IllegalArgumentException("Could not find animation for " + designCode);
            }
        }
    }

    public String getType() {
        return "PRINTED_" + designCode.toString().toUpperCase();
    }
}
