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

    public IntLoc(float x, float y) {
        this((int) x, (int) y);
    }
}
