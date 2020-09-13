package playground;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BlockLocation {
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
}
