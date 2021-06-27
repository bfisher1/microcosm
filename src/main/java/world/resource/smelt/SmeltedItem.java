package world.resource.smelt;

import world.resource.Item;

public abstract class SmeltedItem extends Item {

    public enum SmeltedCode {
        Iron,
        Glass,
        Silicon,
        Brick,
        Rubber
    }

    public abstract SmeltedCode getSmeltedCode();


    @Override
    public String getType() {
        return "SMELTED_" + getSmeltedCode().toString().toUpperCase();
    }
}
