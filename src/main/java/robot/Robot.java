package robot;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import microcosm.Mob;
import util.Loc;
import world.block.Block;

@Getter
@Setter
public class Robot extends Mob {
    private Entity entity;
    private double speed;
    private double direction;

    public Robot(double x, double y) {
        entity = FXGL.entityBuilder()
                .at(x * Block.BLOCK_WIDTH, y * Block.BLOCK_WIDTH)
                .view("wizard_anims/grape still left.png")
                .buildAndAttach();
        direction = 0;
        speed = 2.5;
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

    public void move() {
        move(speed * Math.cos(direction), speed * Math.sin(direction));
    }
}
