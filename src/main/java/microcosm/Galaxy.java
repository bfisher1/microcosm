package microcosm;

import com.almasb.fxgl.dsl.FXGL;
import lombok.Getter;
import lombok.Setter;
import util.IntLoc;
import world.World;
import world.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Galaxy {
    private List<World> worlds;

    // how many pixels we divide the grid by for collisions (should be bigger than anything that collides)
    public static int REGION_SIZE = 500;

    // key is region that mob is in
    private Map<IntLoc, List<Mob>> mobs;

    public Galaxy() {
        mobs = new HashMap<>();
        worlds = new ArrayList<>();
        // background
        FXGL.entityBuilder()
            .at(0, 0)
            .view("galaxies.jpg")
            .buildAndAttach()
            .setZ(0);
    }

    public void runMobCollisions() {
        // collisions with blocks
        // collisions with mobs

        //loop through each mob
            // check if mobs in neighboring regions
                //handle collisions
        mobs.forEach((region, mobList) -> {
            mobList.forEach(mob -> {
                mob.resetCollisions();
                List<IntLoc> neighboringRegions = getNeighboringRegions(region);
                neighboringRegions.forEach(neighbor -> {
                    if(mobs.containsKey(neighbor))
                        mobs.get(neighbor).forEach(otherMob -> {
                            if(otherMob != mob && mob.isCollidingWith(otherMob))
                                mob.handleCollision(otherMob);
                        });
                });
                //
                if(mob.isOnWorld()) {
                    // find which block the mob is on
                    double dx = mob.getCurrentWorld().getX() - mob.getX();
                    double dy = mob.getCurrentWorld().getY() - mob.getY();
                    int x = (int) (dx / Block.BLOCK_WIDTH);
                    int y = (int) (dy / Block.BLOCK_WIDTH);

                    if(mob.getCurrentWorld().isBlockLoaded(x, y)) {
                        Block block = mob.getCurrentWorld().getBlockAt(x, y);

                        block.handleCollision(mob);
                        //mob.handleCollision(block);
                    }
                }
            });
        });
    }

    public void runRender() {
        //
    }

    private List<IntLoc> getNeighboringRegions(IntLoc region) {
        int numberOfNeighborsOnSide = 1;
        List<IntLoc> neighbors = new ArrayList<>();
        for(int i = -numberOfNeighborsOnSide; i <= numberOfNeighborsOnSide; i++) {
            for(int j = -numberOfNeighborsOnSide; j <= numberOfNeighborsOnSide; j++) {
                neighbors.add(new IntLoc(region.getX() + i, region.getY() + j));
            }
        }

        return neighbors;
    }

    public void updateMobRegion(Mob mob, IntLoc prevRegion, IntLoc newRegion) {
        mobs.get(prevRegion).remove(mob);
        if(!mobs.containsKey(newRegion)) {
            mobs.put(newRegion, new ArrayList<>());
        }
        mobs.get(newRegion).add(mob);
    }

    public void addMob(Mob mob) {
        IntLoc region = mob.getGalaxyRegion();
        if(!mobs.containsKey(region)) {
            mobs.put(region, new ArrayList<>());
        }
        mobs.get(region).add(mob);
    }
}
