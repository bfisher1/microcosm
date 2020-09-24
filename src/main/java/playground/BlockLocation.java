package playground;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class BlockLocation implements Location<Integer> {
    int x;
    int y;
    int z;

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof BlockLocation) {
            BlockLocation otherLoc = (BlockLocation) other;
            return x == otherLoc.x && y == otherLoc.y && z == otherLoc.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (x + "," + y + "," + z).hashCode();
    }

    @Override
    public void setX(Integer x) {
        this.x = x;
    }

    @Override
    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public void setZ(Integer z) {
        this.z = z;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getZ() {
        return z;
    }

    @Override
    public Optional<World> getWorld() {
        return Optional.empty();
    }

}
