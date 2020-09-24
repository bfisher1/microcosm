package playground;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class WorldLocation implements Location<Double> {
    // TODO, this should be a double
    double x;
    double y;
    double z;
    private World world;

    @Override
    public Double getX() {
        return x;
    }

    @Override
    public Double getY() {
        return y;
    }

    @Override
    public Double getZ() {
        return z;
    }

    @Override
    public void setX(Double x) {
        this.x = x;
    }

    @Override
    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public void setZ(Double z) {
        this.z = z;
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.of(world);
    }
}
