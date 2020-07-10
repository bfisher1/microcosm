package microcosm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Collision {
    private Collidable object1;
    private Collidable object2;

    public Collidable getObjectCollidingWith(Object object) {
        if(object == object1)
            return object2;
        else if(object == object2)
            return object1;
        throw new IllegalArgumentException("Unknown source object");
    }
}
