package world.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microcosm.Animation;
import world.World;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "electronic_device_block")
public abstract class ElectronicDevice extends Block {

    @Transient
    private boolean on;

    @Transient
    private String onAnim;

    public ElectronicDevice(int x, int y, World world) {
        super(x, y, world);
    }

    public abstract Animation getOnAnimation();

    public abstract Animation getOffAnimation();

    public void whileOn() {
        // nothing as default
    }

    public void setOn(boolean on) {
        this.on = on;
        if (on)
            setAnimation(getOnAnimation());
        else
            setAnimation(getOffAnimation());
        updateAnimation();
    }

}
