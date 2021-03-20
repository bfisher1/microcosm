package util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntLoc {
    private int x;
    private int y;

    public IntLoc(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public IntLoc(double x, double y) {
        this((int) x, (int) y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof IntLoc) {
            IntLoc otherLoc = (IntLoc) other;
            return x == otherLoc.x && y == otherLoc.y;
        }
        return false;
    }

    public void increase(IntLoc diff) {
        this.x += diff.x;
        this.y += diff.y;
    }

    @Override
    public int hashCode() {
        return (x + "" + y).hashCode();
    }

    public Loc toLoc() {
        return new Loc(x, y);
    }
}
