package world;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sun")
public class Sun extends World {

    public Sun() {
        super();
        // todo make larger
        setRadius(4);
        setType(Type.Sun);
    }

    public Sun(double x, double y) {
        this();
        setX(x);
        setY(y);
    }
}
