package world;

public class WorldFactory {


    public static World create(World other) {
        World world = create(other.getX(), other.getY(), other.getType());
        world.setId(other.getId());
        world.setRadius(other.getRadius());
        world.setBlocksByType(other.getBlocksByType());
        world.setChunks(other.getChunks());
        return world;
    }

    public static World create(double x, double y, World.Type type) {
        switch(type) {
            case World:
                return new World(x, y);
            case Sun:
                return new Sun(x, y);
            default:
                throw new IllegalArgumentException("Unknown world type!");
        }
    }
}
