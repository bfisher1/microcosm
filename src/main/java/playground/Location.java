package playground;

import java.util.Optional;

public interface Location<T> {
    T getX();
    T getY();
    T getZ();
    void setX(T x);
    void setY(T y);
    void setZ(T z);
    Optional<World> getWorld();
}
