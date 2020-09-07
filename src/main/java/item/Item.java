package item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import animation.Sprite;
import util.IntLoc;
import util.Loc;

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
        Sprite sprite = item.getSprite();
        if (sprite != null && container.getSprite() != null) {
            sprite.setX(container.getScreenLoc().getX() + locInContainer.getX() + layoutOffset.getX());
            sprite.setY(container.getScreenLoc().getY() + locInContainer.getY() + layoutOffset.getY());
            sprite.setZ(getContainer().getZ() + 1);
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
