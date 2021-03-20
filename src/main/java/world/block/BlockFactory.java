package world.block;

import playground.BlockLocation;
import playground.World;

import static world.block.Block.*;

public class BlockFactory {

    public static Block create(BlockLocation location, Type type, World world) {
        Block block = create(location.getX(), location.getY(), type, world);
        block.setZ(location.getZ());
        return block;
    }


    public static Block create(int x, int y, Type type, World world) {
        Block block = null;
        switch(type) {
            case Grass:
                block = new GrassBlock(x, y, world);
                break;
            case Water:
                block = new WaterBlock(x, y, world);
                break;
            case Coal:
                block = new CoalBlock(x, y, world);
                break;
            case Stone:
                block = new StoneBlock(x, y, world);
                break;
            case Sand:
                block = new SandBlock(x, y, world);
                break;
            case Tree:
                block = new TreeBlock(x, y, world);
                break;
            case Copper:
                block = new CopperBlock(x, y, world);
                break;
            case Zinc:
                block = new ZincBlock(x, y, world);
                break;
            case Silicon:
                block = new SiliconBlock(x, y, world);
                break;
            case Nickel:
                block = new NickelBlock(x, y, world);
                break;
            case Iron:
                block = new IronBlock(x, y, world);
                break;
            case Sun:
                block = new SunBlock(x, y, world);
                break;
            case Uranium:
                block = new UraniumBlock(x, y, world);
                break;
            case Wire:
                block = new WireBlock(x, y, world);
                break;
            case Generator:
                block = new GeneratorBlock(x, y, world);
                break;
            case Treadmill:
                block = new TreadmillBlock(x, y, world);
                break;
            case Injector:
                block = new InjectorBlock(x, y, world);
                break;
            case Computer:
                block = new ComputerBlock(x, y, world);
                break;
            case Tray:
                block = new TrayBlock(x, y, world);
                break;
            case Smelter:
                block = new SmelterBlock(x, y, world);
                break;
            case Printer:
                block = new PrinterBlock(x, y, world);
                break;
            case Clay:
                block = new ClayBlock(x, y, world);
                break;
            case Unknown:
            default:
                block = new UnknownBlock(x, y, world);
        }
        block.setType(type);
        return block;
    }
}
