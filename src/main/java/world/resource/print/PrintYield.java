package world.resource.print;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrintYield {

    double fuelConsumed;
    double resourceQuantityConsumed;
    long timeRequiredMillis;
    PrintedItem printedItem;
    boolean validRecipe = true;
}
