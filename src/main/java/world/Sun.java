package world;

public class Sun extends World {
    public Sun(double x, double y) {
        super(x, y);
        setRadius(getRadius() + 10);
    }
}
