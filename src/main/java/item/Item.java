package item;

import com.almasb.fxgl.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import microcosm.Animation;
import util.IntLoc;
import util.Loc;

@Getter
@Setter
@AllArgsConstructor
public class Item {
    private Itemable item;
    private int quantity;
    private IntLoc layoutOffset;
    private Loc locInContainer;

    public Item(Itemable item, IntLoc layoutOffset) {
        this(item, 1, layoutOffset, new Loc(0, 0));
    }

    public void move(double x, double y) {
        locInContainer.setX(locInContainer.getX() + x);
        locInContainer.setY(locInContainer.getY() + y);
        Entity entity = item.getAnimation().getEntity();
        if (entity != null) {
            entity.setX(entity.getX() + x);
            entity.setY(entity.getY() + y);
        }
    }

}
