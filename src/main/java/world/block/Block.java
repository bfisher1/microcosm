package world.block;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import microcosm.Animation;
import microcosm.Collidable;
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

    private Animation anim = null;

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
        Sun,
        Uranium,
        Plutonium,
        Unknown
    };

    private int x;
    private int y;
    private int z;

    private int animFrames;
    private double animDelay;
    private boolean isAnim;

    private int xSpriteOffset;
    private int ySpriteOffset;
    private Type type;
    private  Block above = null;
    private String animName;
    private Entity entity;
    private World world;

    public Block(int x, int y, World world) {
        this.x = x;
        this.y = y;
        this.world = world;
        z = 1;
        xSpriteOffset = 0;
        ySpriteOffset = 0;
        isAnim = false;
    }
    public void stack(Block block) {
        block.setZ(getZ() + 1);
        this.above = block;
    }

    public void setAnimation(String animName) {
        setAnimName(animName);
    }

    public void setAnimation(String animName, int frames, double delay) {
        //TODO refactor and don't use animDelay or animFrames, redundant
        setAnimName(animName);
        isAnim = true;
        animDelay = delay;
        animFrames = frames;
    }


    public void removeFromScreen() {
        entity.removeFromWorld();
        anim = null;
        world.removeRenderedBlock(this);
        if(above != null)
            above.removeFromScreen();
    }

    public void addToScreen(Camera camera) {
        if (isAnim) {
            setAnim(new Animation(animName, animFrames, animDelay));
            entity = FXGL.entityBuilder()
                    .at(x * BLOCK_WIDTH - camera.getX(), y * BLOCK_WIDTH - camera.getY())
                    .with(anim)
                    .buildAndAttach();
        } else {
            entity = FXGL.entityBuilder()
                    .at(x * BLOCK_WIDTH - camera.getX(), y * BLOCK_WIDTH - camera.getY())
                    .view(animName)
                    .buildAndAttach();
        }
        entity.setZ(getZ());
        world.addRenderedBlock(this);
        if(above != null)
            above.addToScreen(camera);
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
        entity.setX(loc.getX() + xSpriteOffset);
        entity.setY(loc.getY() + ySpriteOffset);
        if(above != null)
            above.setScreenLoc(loc);
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
        if(above != null)
            above.move(x, y);
    }

}
