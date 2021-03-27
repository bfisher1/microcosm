package world.block;

import animation.Animation;
import animation.AnimationBuilder;
import lombok.Getter;
import lombok.Setter;
import playground.World;
import world.resource.WorldItem;
import world.resource.print.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@Setter
public class PrinterBlock extends ElectronicDevice {

    /**
     * Max number of products that can be held before being removed into the block it is facing;
     */
    private int maxProductCapacity = 0;

    /**
     * Max vol held by printer.
     */
    private int maxFuel = 100;

    /**
     * Current fuel held by printer.
     */
    private double fuel;

    /**
     * Max vol held by printer.
     */
    private int resourceMax = 100;

    /**
     * Parts requested to be printed.
     */
    Queue<PrintItemRequest> printRequests = new ConcurrentLinkedQueue<>();

    private Timer timer = new Timer();

    /**
     * Automatically starts printing when new fuel/inputs added.
     */
    private boolean autoPrint = true;


    /**
     * The resource the machine is filled with to print.
     * At a given time, a printer can only print objects of one resource at a time.
     */
    private PrintableResourceCode currentResourceType;

    /**
     * The  quantity of the resource the machine is filled with to print.
     */
    private double resourceQuantity = 0.0;

    /**
     * The products created by the smelting machine. Eventually, this should fill
     * up and must be emptied somewhere.
     */
    private List<PrintedItem> products = new ArrayList<>();

    public PrinterBlock(int x, int y, World world) {
        super(x, y, world);
        setOn(false);
    }

    @Override
    public Animation getOnAnimation() {
        return AnimationBuilder.getBuilder()
                .fileName("printer-printing.png")
                .framesAndDelay(10, 11, .03)
                .build();
    }

    @Override
    public Animation getOffAnimation() {
        return AnimationBuilder.getBuilder()
                .fileName("printer.png")
                .build();
    }

    public void changeResource(PrintableResourceCode newResourceType) {
        stopPrinting();
        this.resourceQuantity = 0;
        this.currentResourceType = newResourceType;
    }

    private void stopPrinting() {
        this.setOn(false);
    }

    public void addItemOnTopOf(WorldItem worldItem) {
        if (worldItem.getItem() instanceof PrintableResource &&
                ((PrintableResource) worldItem.getItem()).getResourceCode().equals(currentResourceType)) {
            resourceQuantity += worldItem.getItem().getQuantity();
            startPrintingRequestedItems();
        } else {
            super.addItemOnTopOf(worldItem);
        }
    }

    public void makeRequest(PrintItemRequest request) {
        printRequests.add(request);
    }

    // todo, return amount consumed
    public void addFuel(double amount) {
        fuel += amount;
        startPrintingRequestedItems();
    }

    public void startPrintingRequestedItems() {
        if (!isOn()) {
            printNextRequestedItem();
        }
    }

    private void printNextRequestedItem() {
        if (!printRequests.isEmpty()) {
            PrintItemRequest request = printRequests.peek();
            PrintYield yield = PrintRecipes.getYield(request.getDesignCode(), request.getResourceCode(), request.getQuantity(), request.getSize());
            if (yield.isValidRecipe() && yield.getFuelConsumed() <= fuel && yield.getResourceQuantityConsumed() <= resourceQuantity) {
                // there is enough fuel and material, so start printing
                this.setOn(true);
                // enqueue event to burn
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // need to check isOn in case printer was stopped before
                        // todo, partially complete items and lose fuel when shutting off
                        if (isOn()) {
                            fuel -= yield.getFuelConsumed();
                            resourceQuantity -= yield.getResourceQuantityConsumed();

                            // output printed items into product tray
                            products.add(yield.getPrintedItem());

                            // output products into neighboring block if there are too many in the tray
                            if (products.size() >= maxProductCapacity) {
                                getHighestNeighborAtOrUnderHeight(getDirection()).ifPresent(neighbor -> {
                                    neighbor.placeItemsOnTopOf(new ArrayList<>(products));
                                });
                                products = new ArrayList<>();
                            }

                            printRequests.remove();
                            // start burning next unit/batch of items
                            printNextRequestedItem();
                        }
                    }
                }, yield.getTimeRequiredMillis());
            } else {
                this.setOn(false);
            }
        } else {
            this.setOn(false);
        }
    }
}
