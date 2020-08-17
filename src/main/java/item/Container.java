package item;

import util.Loc;

import java.util.List;

public interface Container {
    public Loc getScreenLoc();
    void addItem(Item item);
    void removeItem(Item item);
    List<Item> getItems();
}
