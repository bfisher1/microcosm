package world.block;

import animation.Animation;
import animation.AnimationBuilder;
import lombok.Getter;
import lombok.Setter;
import playground.World;
import world.resource.WorldItem;
import world.resource.smelt.SmeltRecipes;
import world.resource.smelt.SmeltableItem;
import world.resource.smelt.SmeltedItem;
import world.resource.smelt.Yield;

import java.util.*;

@Getter
@Setter
public class SmelterBlock extends ElectronicDevice {

    /**
     * How much fuel is in the machine.
     */
    private int fuel = 0;

    /**
     * The resources being smelted together, their quantities.
     */
    private List<SmeltableItem> resources = new ArrayList<>();

    /**
     * The products created by the smelting machine. Eventually, this should fill
     * up and must be emptied somewhere.
     */
    private List<SmeltedItem> products = new ArrayList<>();

    /**
     * Max number of products that can be held before being removed into the block it is facing;
     */
    private int maxProductCapacity = 0;

    private Timer timer = new Timer();

    /**
     * Automatically smelt inputs when fuel/inputs added.
     */
    private boolean autoSmelt = true;

    public SmelterBlock(int x, int y, World world) {
        super(x, y, world);
        setOn(false);
        setDirection(Direction.Up);
        setDrawZOffset(1);
    }

    @Override
    public Animation getOnAnimation() {
        return AnimationBuilder.getBuilder().fileName("smelter-on.png").build();
    }

    @Override
    public Animation getOffAnimation() {
        return AnimationBuilder.getBuilder().fileName("smelter-off.png").build();
    }

    public void addFuel(int amount) {
        fuel += amount;
        if (autoSmelt) {
            startBurningUntilAllSmelted();
        }
    }

    public void addItem(SmeltableItem item) {
        Optional<SmeltableItem> existingItem = resources.stream().filter(resource -> resource.getSmeltCode().equals(item.getSmeltCode())).findAny();
        if (existingItem.isPresent()) {
            // add quantity from new item
            existingItem.get().setQuantity(item.getQuantity() + existingItem.get().getQuantity());
        } else {
            resources.add(item);
        }

        if (autoSmelt) {
            startBurningUntilAllSmelted();
        }
    }

    public void startBurningUntilAllSmelted() {
        if (!isOn()) {
            burnUntilAllSmelted();
        }
    }

    private void burnUntilAllSmelted() {
        Yield yield = SmeltRecipes.getYield(resources);
        // todo, handle not enough resources. What happens if there are 0 quantity of a resource, or too little? Should be removed
        if (yield.isValidRecipe() && yield.getFuelConsumed() <= fuel) {
            // there is enough fuel and resources, so start burning
            this.setOn(true);
            setAnimation("smelter-on.png");
            // enqueue event to burn
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // need to check isOn in case printer was stopped before
                    // todo, partially complete items and lose fuel when shutting off
                    if (isOn()) {
                        fuel -= yield.getFuelConsumed();
                        resources.stream().forEach(resource -> {
                            resource.setQuantity(resource.getQuantity() - yield.getQuantitiesConsumed().get(resource.getSmeltCode()));
                        });

                        // output products into product tray
                        yield.getSmeltedItems().stream().forEach(output -> {
                            Optional<SmeltedItem> existingProduct = products.stream().filter(product -> product.getSmeltedCode().equals(output.getSmeltedCode())).findAny();
                            if (existingProduct.isPresent()) {
                                existingProduct.get().setQuantity(output.getQuantity() + existingProduct.get().getQuantity());
                            } else {
                                products.add(output);
                            }
                        });

                        // output products into neighboring block if there are too many in the tray
                        if (products.size() >= maxProductCapacity) {
                            getHighestNeighborAtOrUnderHeight(getDirection()).ifPresent(neighbor -> {
                                neighbor.placeItemsOnTopOf(new ArrayList<>(products));
                            });
                            products = new ArrayList<>();
                        }

                        // start burning next unit/batch of items
                        burnUntilAllSmelted();
                    }
                }
            }, yield.getTimeRequiredMillis());
        } else {
            this.setOn(false);
        }
    }

    public void addItemOnTopOf(WorldItem worldItem) {
        if (worldItem.getItem() instanceof SmeltableItem) {
            addItem((SmeltableItem) worldItem.getItem());
        } else {
            super.addItemOnTopOf(worldItem);
        }
    }
}
