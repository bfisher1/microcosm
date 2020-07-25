package world;

import javax.persistence.Entity;

@Entity
public class Sun extends World {

    public Sun() {
        super();
        // todo make larger
        setRadius(10);
        setType(Type.Sun);
    }

    public Sun(double x, double y) {
        this();
    }
}
