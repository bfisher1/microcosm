package world.block;

import lombok.Getter;
import lombok.Setter;
import playground.World;
import world.resource.Item;
import world.resource.smelt.SmeltRecipes;
import world.resource.smelt.SmeltableItem;
import world.resource.smelt.SmeltedItem;
import world.resource.smelt.Yield;

import java.util.*;

@Getter
@Setter
public class SmelterBlock extends Block {

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

    private boolean on = false;
    private Timer timer = new Timer();

    public SmelterBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("smelter-off.png");
        setDirection(Direction.Up);
    }

    public void addFuel(int amount) {
        fuel += amount;
    }

    public void addItem(SmeltableItem item) {
        Optional<SmeltableItem> existingItem = resources.stream().filter(resource -> resource.getSmeltCode().equals(item.getSmeltCode())).findAny();
        if (existingItem.isPresent()) {
            // add quantity from new item
            existingItem.get().setQuantity(item.getQuantity() + existingItem.get().getQuantity());
        } else {
            resources.add(item);
        }
    }

    public void startBurningUntilAllSmelted() {
        if (!isOn()) {
            burnUntilAllSmelted();
        }
    }

    private void burnUntilAllSmelted() {
        Yield yield = SmeltRecipes.getYield(resources);
        if (yield.isValidRecipe() && yield.getFuelConsumed() <= fuel) {
            // there is enough fuel, so start burning
            this.setOn(true);
            setAnimation("smelter-on.png");
            // enqueue event to burn
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
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
            }, yield.getTimeRequiredMillis());
        } else {
            turnOff();
        }
    }

    public void turnOff() {
        this.setOn(false);
        setAnimation("smelter-off.png");
    }
}
