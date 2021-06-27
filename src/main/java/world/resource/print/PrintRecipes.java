package world.resource.print;

public class PrintRecipes {
    //
    public static PrintYield getYield(PrintDesignCode designCode, PrintableResourceCode resourceCode, int quantity, Size size) {
        PrintYield yield = new PrintYield();

        double volume = 1.0;

        switch (designCode) {
            case Gear:
                volume = .5;
                break;
            case Cap:
                volume = .6;
                break;
            case Disc:
                volume = 1.5;
                break;
            case Tire:
                volume = 3.4;
                break;
            case Ball:
                volume = 4.5;
                break;
            case Stick:
                volume = 15.0;
                break;
            case Drill:
                volume = 16;
                break;
            case Panel:
                volume = 4;
                break;
        }

        switch (size) {
            case Small:
                volume *= .3;
                break;
            case Large:
                volume *= 3;
                break;
        }

        PrintedItem printedItem = new PrintedItem();
        printedItem.setDesignCode(designCode);

        double fuelConsumed = volume;
        double timeTaken = volume * 500;

        switch (resourceCode) {
            case Iron:
                fuelConsumed = 3.5;
                timeTaken = 3.5;
                printedItem.setResourceCode(PrintedResourceCode.Iron);
                break;
            case Rubber:
                fuelConsumed = 2.5;
                timeTaken = 1.5;
                printedItem.setResourceCode(PrintedResourceCode.Rubber);
                break;
            case Clay:
                fuelConsumed = 3.4;
                timeTaken = 2.5;
                printedItem.setResourceCode(PrintedResourceCode.Brick);
                break;
            case Plastic:
                fuelConsumed = 0.5;
                timeTaken = 0.5;
                printedItem.setResourceCode(PrintedResourceCode.Plastic);
                break;
            case Silicon:
                fuelConsumed = 2.4;
                timeTaken = 1.5;
                printedItem.setResourceCode(PrintedResourceCode.Silicon);
                break;
            case Copper:
                fuelConsumed = 3.4;
                timeTaken = 3.5;
                printedItem.setResourceCode(PrintedResourceCode.Copper);
                break;
        }

        double totalVolume = volume * quantity;

        yield.setFuelConsumed(fuelConsumed);
        yield.setTimeRequiredMillis((long) (Math.max(timeTaken, 1000))); // minimum of 1 second per print
        printedItem.setQuantity(quantity);
        printedItem.setSize(size);

        yield.setPrintedItem(printedItem);

        yield.setResourceQuantityConsumed(totalVolume);
        yield.setValidRecipe(true);

        return yield;
    }
}
