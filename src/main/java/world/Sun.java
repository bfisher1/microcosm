package world;

import javax.persistence.Entity;

@Entity
public class Sun extends World {
    public Sun(double x, double y) {
        super(x, y);
        setRadius(10);
        //setRadius(getRadius() + 10);
        getBlocksByType();
        setType(Type.Sun);
    }
}
