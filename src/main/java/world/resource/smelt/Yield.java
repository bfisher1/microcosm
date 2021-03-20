package world.resource.smelt;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Yield {
    double fuelConsumed;
    Map<SmeltableItem.SmeltCode, Double> quantitiesConsumed = new HashMap<>();
    long timeRequiredMillis;
    List<SmeltedItem> smeltedItems;
    boolean validRecipe = true;
}
