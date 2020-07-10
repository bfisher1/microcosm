package microcosm;


import lombok.Getter;
import lombok.Setter;
import util.IntLoc;
import world.World;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Mob implements Collidable {

    private double x;
    private double y;

    private double prevX;
    private double prevY;

    private Galaxy galaxy;
    private List<Collidable> collisions;
    private World currentWorld;

    public IntLoc getGalaxyRegion() {
        return new IntLoc(x / Galaxy.REGION_SIZE,  y / Galaxy.REGION_SIZE);
    }

    public void resetLoc() {
        x = prevX;
        y = prevY;
    }

    public void setLoc(double x, double y) {
        IntLoc prevGalaxyRegion = getGalaxyRegion();

        this.prevX = x;
        this.prevY = y;

        this.x = x;
        this.y = y;

        // update galaxy map of mobs
        IntLoc newGalaxyRegion = getGalaxyRegion();
        if(!prevGalaxyRegion.equals(newGalaxyRegion)) {
            galaxy.updateMobRegion(this, prevGalaxyRegion, newGalaxyRegion);
        }
    }

    public void resetCollisions() {
        setCollisions(new ArrayList<>());
    }

    public void handleCollision(Collidable collider) {
        System.out.println("COlliding");
        //collider.
    }

    public boolean isOnWorld() {
        return currentWorld != null;
    }


    @Override
    public boolean isCollidingWith(Collidable otherMob) {
        return true;
    }
}
