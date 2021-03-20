package world.resource.smelt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SmeltRecipes {
    // product yielded by 1 cycle (i.e. smallest amount that can be made at once)
    public static Yield getYield(List<SmeltableItem> items) {
        Yield yield = new Yield();

        if (elementsAre(items, SmeltableItem.SmeltCode.Iron)) {
            SmeltableItem iron = getItem(items, SmeltableItem.SmeltCode.Iron);
            if (iron.getQuantity() >= 1) {
                yield.setFuelConsumed(.5); // 1/2 fuel consumer per iron
                yield.setTimeRequiredMillis(500); // 1/2 second per iron
                yield.getQuantitiesConsumed().put(SmeltableItem.SmeltCode.Iron, 1.0);
                yield.setSmeltedItems(Collections.singletonList(new Iron(1.0)));
            } else {
                yield.setValidRecipe(false);
            }
        }
        else if (elementsAre(items, SmeltableItem.SmeltCode.Sap, SmeltableItem.SmeltCode.Water)) {
            //
        } else {
            yield.setValidRecipe(false);
        }
        return yield;
    }

    private static boolean elementsAre(List<SmeltableItem> items, SmeltableItem.SmeltCode... itemCodes) {
        return items.size() == itemCodes.length &&
                Arrays.stream(itemCodes).allMatch(itemCode ->
                        items.stream().anyMatch(item -> item.getSmeltCode().equals(itemCode))
                );
    }

    private static SmeltableItem getItem(List<SmeltableItem> items, SmeltableItem.SmeltCode smeltCode) {
        return items.stream().filter(item -> item.getSmeltCode().equals(smeltCode)).findFirst().get();
    }
}
