package playground;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor

/*
 * Location drifting in space, not in world;
 */
public class GalacticLocation implements Location<Double> {
    Double x;
    Double y;
    Double z;

    @Override
    public Optional<World> getWorld() {
        return Optional.empty();
    }
}
