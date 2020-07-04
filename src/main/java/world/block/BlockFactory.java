package world.block;

import world.World;

import static world.block.Block.*;

public class BlockFactory {
    public static Block create(int x, int y, Type type, World world) {
        Block block = null;
        switch(type) {
            case Grass:
                block = new GrassBlock(x, y);
                break;
            case Water:
                block = new WaterBlock(x, y);
                break;
            case Coal:
                block = new CoalBlock(x, y);
                break;
            case Stone:
                block = new StoneBlock(x, y);
                break;
            case Sand:
                block = new SandBlock(x, y);
                break;
            case Tree:
                block = new TreeBlock(x, y);
                break;
            case Copper:
                block = new CopperBlock(x, y);
                break;
            case Zinc:
                block = new ZincBlock(x, y);
                break;
            case Silicon:
                block = new SiliconBlock(x, y);
                break;
            case Nickel:
                block = new NickelBlock(x, y);
                break;
            case Iron:
                block = new IronBlock(x, y);
                break;
            case Unknown:
            default:
                block = new UnknownBlock(x, y);
        }
        block.setType(type);
        return block;
    }
}
