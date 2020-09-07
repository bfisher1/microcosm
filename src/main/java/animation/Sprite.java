package animation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@Getter
@Setter
public class Sprite implements Comparable<Sprite> {
    private Animation animation;
    private int x;
    private int y;
    private double z;
    public static int count = 0;

    public Sprite() {
        count++;
    }

    public Sprite(Sprite sprite) {
        this.animation = sprite.animation;
        this.x = sprite.x;
        this.y = sprite.y;
        this.z = sprite.z;
    }

    public Sprite(Animation animation, int x, int y, double z) {
        this();
        this.animation = animation;
        this.x = x;
        this.y = y;
        this.z = z + ( (double) Sprite.count) * .00001;
    }

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
        return (int) (this.getZ() - sprite.getZ() );
    }

    public void draw(Graphics g) {
        getAnimation().draw(g, x, y);
    }

}
