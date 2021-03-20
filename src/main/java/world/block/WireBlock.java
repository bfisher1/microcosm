package world.block;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import animation.Animation;
import animation.AnimationBuilder;
import playground.World;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "wire_block")
@NoArgsConstructor
public class WireBlock extends ElectronicDevice {

    public WireBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation(getAnimationName());
        setDirection(Direction.Left);
        setFullyCoveringView(false);
    }

    @Override
    public Animation getOnAnimation() {
        return AnimationBuilder.getBuilder().fileName(getAnimationName()).build();
    }

    private String getAnimationName() {
        String on = isOn() ? "on" : "off";
        boolean left = false;
        boolean right = false;
        boolean up = false;
        boolean down = false;
//        if(hasNeighborBlock(-1, 0)) {
//            if(getNeighborBlock(-1, 0).getType().equals(Type.Wire)) {
//                left = true;
//            }
//        }
//        if(hasNeighborBlock(1, 0)) {
//            if(getNeighborBlock(1, 0).getType().equals(Type.Wire)) {
//                right = true;
//            }
//        }
//        if(hasNeighborBlock(0, -1)) {
//            if(getNeighborBlock(0, -1).getType().equals(Type.Wire)) {
//                up = true;
//            }
//        }
//        if(hasNeighborBlock(0, 1)) {
//            if(getNeighborBlock(0, 1).getType().equals(Type.Wire)) {
//                down = true;
//            }
//        }
        if (left) {
            if (up) {
                return "wire up left " + on + ".png";
            } else if (down) {
                return"wire down left " + on + ".png";
            } else {
                return "wire-horizontal-" + on + ".png";
            }
        }
        else if (right) {
            if (up) {
                return "wire up right " + on + ".png";
            } else if (down) {
                return "wire down right " + on + ".png";
            } else {
                return "wire-horizontal-" + on + ".png";
            }
        }
        else if(up || down) {
            return "wire-vertical-" + on + ".png";
        }
        return "wire no dir " + on + ".png";
    }

    @Override
    public Animation getOffAnimation() {
        return AnimationBuilder.getBuilder().fileName(getAnimationName()).build();
    }

    public void setOn(boolean on) {
        super.setOn(on);
//        getNeighbors().forEach(block -> {
//            if(block.isElectronicDevice()) {
//                ElectronicDevice electronicDevice = (ElectronicDevice) block;
//                if(!electronicDevice.isOn())
//                    electronicDevice.setOn(true);
//            }
//        });
    }

}
