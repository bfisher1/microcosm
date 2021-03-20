package world.resource.smelt;

import world.resource.Item;

public abstract class SmeltableItem extends Item {

    public abstract SmeltCode getSmeltCode();

    public enum SmeltCode {
        Iron,
        Sand,
        Clay,
        Water,
        Sap
    }
}

