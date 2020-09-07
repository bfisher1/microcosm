package item;

import animation.Sprite;
import util.Loc;

import java.util.List;

public interface Container {
    Loc getScreenLoc();
    int getZ();
    void addItem(Item item);
    void removeItem(Item item);
    List<Item> getItems();
    Sprite getSprite();
}
