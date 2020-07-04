package world.block;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import util.Loc;

@Getter
@Setter
public class Block {

    public static int BLOCK_WIDTH = 32;

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

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void stack(Block block) {
        this.above = block;
    }

    public void loadOnScreen() {
        entity = FXGL.entityBuilder()
                .at(x * BLOCK_WIDTH, y * BLOCK_WIDTH)
                .view(animName)
                .buildAndAttach();
    }

    public Loc getLoc() {
        return new Loc(entity.getX(), entity.getY());
    }

    public void setLoc(Loc loc) {
        entity.setX(loc.getX());
        entity.setY(loc.getY());
    }

    public void move(Loc diff) {
        Loc loc = getLoc();
        loc.increase(diff);
        setLoc(loc);
    }

    public void move(double x, double y) {
        move(new Loc(x, y));
    }

}
