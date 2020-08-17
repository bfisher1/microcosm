package item;

import com.almasb.fxgl.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import microcosm.Animation;
import player.Camera;
import util.IntLoc;
import util.Loc;
import world.block.Block;
import world.block.TreadmillBlock;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Item {
    private Itemable item;
    private int quantity;
    private IntLoc layoutOffset;
    private Loc locInContainer;
    private List<Container> containersRemovedFrom;
    private Container container;

    public Item(Itemable item, IntLoc layoutOffset, Container container) {
        this(item, 1, layoutOffset, new Loc(0, 0), new ArrayList<>(), container);
    }

    public void move(double x, double y) {
        locInContainer.setX(locInContainer.getX() + x);
        locInContainer.setY(locInContainer.getY() + y);
        Entity entity = item.getAnimation().getEntity();
        if (entity != null) {
            entity.setX(container.getScreenLoc().getX() + locInContainer.getX() + layoutOffset.getX());
            entity.setY(container.getScreenLoc().getY() + locInContainer.getY() + layoutOffset.getY());
        }
    }

    public boolean isMarkedAsRemoved(Container container) {
        return containersRemovedFrom.contains(container);
    }

    public void markAsRemoved(Container container) {
        containersRemovedFrom.add(container);
        this.container = container;
    }

    public void unmarkAsRemoved(Container container) {
        containersRemovedFrom.remove(container);
    }
}
