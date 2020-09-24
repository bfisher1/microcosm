package playground;

import animation.Sprite;
import lombok.Getter;
import lombok.Setter;

import java.util.PriorityQueue;

@Getter
@Setter
public class Camera {
    private double x;
    private double y;
    private PriorityQueue<Sprite> sprites = new PriorityQueue<>();

    private static Camera instance;

    private Camera(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera(0, 0);
        }
        return instance;
    }

    public void addSpriteToScreen(Sprite sprite) {
        sprites.add(sprite);
    }

    public void removeSpriteFromScreen(Sprite sprite) {
        if (sprites.contains(sprite))
            try {
                sprites.remove(sprite);
            } catch(Exception e) {
                System.out.println(e);
            }
    }

}
