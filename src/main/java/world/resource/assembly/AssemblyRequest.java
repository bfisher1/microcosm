package world.resource.assembly;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class AssemblyRequest {
    private AssembledCode assembledCode;
    private int quantity;
    List<AssemblyWork> assemblyWork;
    Map<String, Integer> inputItemTypeQuantities;
}
