package world.block;

import item.Container;
import item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microcosm.Animation;
import world.World;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "treadmill_block")
@NoArgsConstructor
public class TreadmillBlock extends ElectronicDevice implements Container {

    @Transient
    private List<Item> items;

    public TreadmillBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation(getOffAnimation());
        items = new ArrayList<>();
    }

    @Override
    public Animation getOnAnimation() {
        return new Animation("treadmill-up.png", 10, .2);
    }

    @Override
    public Animation getOffAnimation() {
        return new Animation("treadmill-up-still.png");
    }
}
