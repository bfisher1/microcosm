package world.block;

import lombok.Getter;
import lombok.Setter;
import microcosm.Animation;
import world.World;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public abstract class ElectronicDevice extends Block {

    private boolean on;
    private String onAnim;

    public ElectronicDevice(int x, int y, World world) {
        super(x, y, world);
    }

    public abstract Animation getOnAnimation();

    public abstract Animation getOffAnimation();

    public void setOn(boolean on) {
        this.on = on;
        if (on)
            setAnimation(getOnAnimation());
        else
            setAnimation(getOffAnimation());
        updateAnimation();
    }

}
