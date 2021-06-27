package world.resource.assembly;


import lombok.Getter;
import lombok.Setter;
import world.resource.print.PrintedItem;

import java.util.List;

@Getter
@Setter
public class AssembledYield {
    double electricityConsumed;
    long timeRequiredMillis;
    AssembledItem assembledItem;
    List<AssemblyWork> assemblyWork;
    boolean validRecipe = true;
}
