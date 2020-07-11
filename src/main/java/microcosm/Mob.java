package microcosm;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import player.Camera;
import util.IntLoc;
import util.Loc;
import world.World;
import world.block.Block;

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

    private Entity entity;
    //private List<Camera> cameras = new ArrayList<>();
    private Camera camera;

    public Mob(double x, double y, String animName) {
        setEntity(FXGL.entityBuilder()
                .at(x , y)
                .view(animName)
                .buildAndAttach());
        setX(x);
        setY(y);
    }

    public void setX(double x) {
        this.prevX = this.x;
        this.x = x;
        if(loadedOnScreen()) {
            entity.setX(x - camera.getX() - entity.getWidth() / 2);
        }
    }

    public void setY(double y) {
        this.prevY = this.y;
        this.y = y;
        if(loadedOnScreen()) {
            entity.setY(y - camera.getY() - entity.getHeight() / 2);
        }
    }

    public IntLoc getGalaxyRegion() {
        return new IntLoc(x / Galaxy.REGION_SIZE,  y / Galaxy.REGION_SIZE);
    }

    public void resetLoc() {
        x = prevX;
        y = prevY;
    }

    public boolean loadedOnScreen() {
        return entity != null && camera != null;
    }

    public void removeFromScreen() {
        entity.removeFromWorld();
        entity = null;
    }

    public void setScreenLoc(double x, double y) {
        entity.setX(x);
        entity.setY(y);
    }

    public Loc getScreenLoc() {
        return new Loc(entity.getX(), entity.getY());
    }

    public void move(Loc diff) {
        setX(x + diff.getX());
        setY(y + diff.getY());
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

    public void handleCollision(Block block) {
       // System.out.println("COlliding block");
        Block.Type type = block.getType();
       if(Block.Type.Stone.equals(block.getType())) {
           resetLoc();
       }
    }

    public void handleCollision(Mob mob) {
        //System.out.println("COlliding mob");
    }

    public boolean isOnWorld() {
        return currentWorld != null;
    }


    @Override
    public boolean isCollidingWith(Collidable otherMob) {
        return true;
    }
}
