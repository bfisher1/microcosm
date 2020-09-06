package world.block;

import lombok.NoArgsConstructor;
import animation.Animation;
import animation.AnimationBuilder;
import world.World;
import world.resource.Mold;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name = "computer_block")
@NoArgsConstructor
public class ComputerBlock extends ElectronicDevice {

    @Transient
    private Map<Mold, Integer> productCounts = new HashMap<>();

    @Transient
    public Map<Mold, Integer> partCounterPerBot = new HashMap<>();


    public ComputerBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("computer-on.png");
        partCounterPerBot.put(Mold.Gear, 2);
        partCounterPerBot.put(Mold.Siding, 2);
        partCounterPerBot.put(Mold.Cap, 2);
        partCounterPerBot.put(Mold.Disc, 2);
        partCounterPerBot.put(Mold.Drill, 1);
        partCounterPerBot.put(Mold.Gripper, 2);
        partCounterPerBot.put(Mold.Pole, 2);
        partCounterPerBot.put(Mold.SmallBall, 2);
        partCounterPerBot.put(Mold.Tire, 2);
    }

    @Override
    public Animation getOnAnimation() {
        return AnimationBuilder.getBuilder().fileName("computer-on.png").build();
    }

    @Override
    public Animation getOffAnimation() {
        return AnimationBuilder.getBuilder().fileName("computer-off.png").build();
    }

    public void itemCreated(InjectorBlock block, Mold mold) {
        if (!productCounts.containsKey(mold)) {
            productCounts.put(mold, 0);
        }
        productCounts.put(mold, productCounts.get(mold) + 1);

        if (productCounts.get(mold) >= partCounterPerBot.get(mold)) {
            productCounts.put(mold, 0);
            switch(mold) {
                case Gear:
                    block.setMold(Mold.Siding);
                    break;
                case Siding:
                    block.setMold(Mold.Cap);
                    break;
                case Cap:
                    block.setMold(Mold.Disc);
                    break;
                case Disc:
                    block.setMold(Mold.Drill);
                    break;
                case Drill:
                    block.setMold(Mold.Gripper);
                    break;
                case Gripper:
                    block.setMold(Mold.Pole);
                    break;
                case Pole:
                    block.setMold(Mold.SmallBall);
                    break;
                case SmallBall:
                    block.setMold(Mold.Tire);
                    break;
                case Tire:
                    block.setMold(Mold.Gear);
                    break;
            }

        }
    }
}