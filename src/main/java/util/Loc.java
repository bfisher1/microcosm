package util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Loc {
    private double x;
    private double y;

    public Loc(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public IntLoc toIntLoc() {
        return new IntLoc((int) x, (int) y);
    }

    public void increase(Loc diff) {
        this.x += diff.x;
        this.y += diff.y;
    }

    public void increase(double diffX, double diffY) {
        this.x += diffX;
        this.y += diffY;
    }

    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    public Loc plus(double xDiff, double yDiff) {
        return new Loc(x + xDiff, y + yDiff);
    }
}
