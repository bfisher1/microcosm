package world.block;

import animation.Animation;
import animation.AnimationBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import playground.World;
import util.Loc;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Random;

@Entity
@Table(name = "tree_block")
@NoArgsConstructor
@Setter
@Getter
public class TreeBlock extends Block {

    public static int SPRITE_OFFSET_MAX = BLOCK_WIDTH / 2;

    public enum State {
        Wilted,
        Pine,
        RedTree,
        Alien
    }

    private State state;

    public TreeBlock(int x, int y, World world) {
        super(x, y, world);
        this.setFullyCoveringView(false);
        double rand = (new Random()).nextDouble();
        if (rand < .3)
            setState(State.RedTree);
        else if (rand < .6)
            setState(State.Alien);
        else
            setState(State.Pine);

        setXSpriteOffset((new Random()).nextInt(SPRITE_OFFSET_MAX) - SPRITE_OFFSET_MAX / 2);
        setYSpriteOffset((new Random()).nextInt(SPRITE_OFFSET_MAX) - SPRITE_OFFSET_MAX / 2 - 100);

    }

    public void setState(State state) {
        if (state != this.state) {
            if (state.equals(State.Pine) || true) {
                setAnimation(AnimationBuilder.getBuilder().fileName("tree-big.png").xOffset(5).yOffset(-25).build());
            }
            else if(state.equals(State.RedTree)) {
                setAnimation("tree-big.png"); //setAnimation("wizard_anims/tree.png");
            } else if(state.equals(State.Wilted)) {
                setAnimation("3d/wilted.png");
            }
            else if(state.equals(State.Alien)) {
                setAnimation("tree-big.png"); //setAnimation("ecosystem data/tree.png");
            }
        }
        this.state = state;
    }

    public void setScreenLoc(Loc loc) {
        super.setScreenLoc(loc);
        getSprite().setZ(getZ() + (int) getSprite().getY());
    }

}
