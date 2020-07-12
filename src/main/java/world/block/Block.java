package world.block;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.Texture;
import lombok.Getter;
import lombok.Setter;
import microcosm.Collidable;
import microcosm.GameApp;
import microcosm.Mob;
import player.Camera;
import util.IntLoc;
import util.Loc;
import world.World;

@Getter
@Setter
public class Block implements Collidable {

    public static int BLOCK_WIDTH = 32;

    @Override
    public boolean isCollidingWith(Collidable otherMob) {
        return false;
    }

    public void handleCollision(Mob mob) {
        mob.handleCollision(this);
    }


    public enum Type {
        Grass,
        Water,
        Coal,
        Stone,
        Sand,
        Tree,
        Copper,
        Zinc,
        Silicon,
        Nickel,
        Iron,
        Unknown
    };

    private int x;
    private int y;
    private Type type;
    private  Block above = null;
    private String animName;
    private Entity entity;
    private World world;

    public Block(int x, int y, World world) {
        this.x = x;
        this.y = y;
        this.world = world;
    }
    public void stack(Block block) {
        this.above = block;
    }


    public void removeFromScreen() {
        entity.removeFromWorld();
        world.removeRenderedBlock(this);
    }

    public void addToScreen(Camera camera) {
        entity = FXGL.entityBuilder()
                .at(x * BLOCK_WIDTH - camera.getX(), y * BLOCK_WIDTH - camera.getY())
                .view(animName)
                .buildAndAttach();
        world.addRenderedBlock(this);
    }

    public void changeEntity(String animName) {
        if(entity == null)
            return;

//        double x = entity.getX();
//        double y = entity.getY();
//
//        Object children = entity.getViewComponent().getChildren();
//        entity.getViewComponent().clearChildren();
//        entity.getViewComponent().addChild(GameApp.water);
//        entity.setX(x);
//        entity.setY(y);
//
//        if(true)
//            return;
        this.animName = animName;
        double x = entity.getX();
        double y = entity.getY();
        entity.removeFromWorld();
        entity = FXGL.entityBuilder()
                .at(x, y)
                .view(animName)
                .buildAndAttach();
    }

    public Loc getScreenLoc() {
        return new Loc(entity.getX(), entity.getY());
    }

    public IntLoc getIntLoc(){
        return new IntLoc(x, y);
    }

    public void setScreenLoc(Loc loc) {
        entity.setX(loc.getX());
        entity.setY(loc.getY());
    }

    public void move(Loc diff) {
        Loc loc = getScreenLoc();
        loc.increase(diff);
        setScreenLoc(loc);
    }

    public boolean onScreen() {
        return entity != null && entity.getX() > 0 && entity.getY() >  0 && entity.getX() < FXGL.getAppWidth() && entity.getY() < FXGL.getAppHeight();
    }

    public void move(double x, double y) {
        move(new Loc(x, y));
    }

}
