package animation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
public class Sprite implements Comparable<Sprite> {
    private Animation animation;
    private int x;
    private int y;
    private int z;

    public void setX(double x) {
        this.x = (int) x;
    }

    public void setY(double y) {
        this.y = (int) y;
    }

    public double getWidth() {
        return animation.getCurrentFrame().getWidth();
    }

    public double getHeight() {
        return animation.getCurrentFrame().getHeight();
    }

    @Override
    public int compareTo(@NotNull Sprite sprite) {
        return this.getZ() - sprite.getZ();
    }

    public void draw(Graphics g) {
        getAnimation().draw(g, x, y);
    }

}
