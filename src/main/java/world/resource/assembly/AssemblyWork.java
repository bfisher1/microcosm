package world.resource.assembly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AssemblyWork {
    AssemblyAction assemblyAction;
    long duration;
    int times;
}
