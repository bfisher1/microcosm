package world.resource;

import animation.Animation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abstract because an item has to exist somewhere (i.e. an inventory, a block, outer space)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Item {
    private Animation animation;
    private double quantity;
}
