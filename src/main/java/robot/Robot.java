package robot;

import com.almasb.fxgl.dsl.FXGL;
import lombok.Getter;
import lombok.Setter;
import microcosm.Mob;
import util.Loc;
import world.block.Block;

@Getter
@Setter
public class Robot extends Mob {
    private double speed;
    private double direction;

    public Robot(double x, double y) {
        super(x, y, "bot2.png");
        direction = 0;
        speed = 1;
    }



    public void move(double x, double y) {
        move(new Loc(x, y));
    }

    public void move() {
        move(speed * Math.cos(direction), speed * Math.sin(direction));
    }

    @Override
    public void handleCollision(Block block) {
        super.handleCollision(block);
        // System.out.println("COlliding block");
        Block.Type type = block.getType();
        if(Block.Type.Stone.equals(block.getType())) {
            setDirection(getDirection() + 2);
        }
    }
}
