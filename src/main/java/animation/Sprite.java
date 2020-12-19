package animation;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Sprite implements Comparable<Sprite> {
    private Animation animation;
    private int x;
    private int y;

    private double sortX;
    private double sortY;

    private double z;
    private double angle = 57;
    private int xOffset;
    private int yOffset;
    public static int count = 0;
    public List<Sprite> backgroundSprites = new ArrayList<>();
    public List<Sprite> foregroundSprites = new ArrayList<>();

    public Sprite() {
        count++;
    }

    public Sprite(Sprite sprite) {
        this.animation = sprite.animation;
        this.x = sprite.x;
        this.y = sprite.y;
        this.z = sprite.z;
        this.xOffset = sprite.xOffset;
        this.yOffset = sprite.yOffset;
    }

    public Sprite(Animation animation, int x, int y, double z) {
        this();
        this.animation = animation;
        this.x = x;
        this.y = y;
        this.z = z + ( (double) Sprite.count) * .001;
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
        double magicScale = 1000000;
        int zDiff = (int) (this.getZ() - sprite.getZ());
        if (Math.abs(zDiff) < 0.001) {
            int yDiff = (int) ((this.sortY - sprite.sortY ) * magicScale);
            if (yDiff == 0) {
                return (int) ((this.sortX - sprite.sortX ) * magicScale);
            }
            return yDiff;
        }
        return zDiff;
    }

    public void draw(Graphics2D g) {
        try {
            backgroundSprites.forEach(sprite -> sprite.draw(g));
            getAnimation().draw(g, x + getAnimation().getXOffset(), y + getAnimation().getYOffset(), this.angle);
            foregroundSprites.forEach(sprite -> sprite.draw(g));
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void addBackgroundSprite(Animation background) {
        Sprite sprite = new Sprite(background, this.x, this.y, this.z);
        backgroundSprites.add(sprite);
    }

    public void addForegroundSprite(Animation background) {
        Sprite sprite = new Sprite(background, this.x, this.y, this.z);
        foregroundSprites.add(sprite);
    }
}
