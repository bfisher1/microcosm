package world.block;

import com.almasb.fxgl.dsl.FXGL;
import item.Container;
import item.Item;
import item.Itemable;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microcosm.Animation;
import util.IntLoc;
import util.Rand;
import world.World;
import world.resource.CastItem;
import world.resource.Mold;
import world.resource.ResourceCost;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Setter
@Entity
@Table(name = "injector_block")
@NoArgsConstructor
public class InjectorBlock extends ElectronicDevice {

    @Transient
    private int fuel = 0;
    @Transient
    private Queue<Item> itemsToInject = new LinkedBlockingQueue<>();
    @Transient
    private Mold mold =  Mold.Gear;

    public InjectorBlock(int x, int y, World world) {
        super(x, y, world);
        setDisplayItems(false);
        setAnimation(getOffAnimation());
    }

    @Override
    public Animation getOnAnimation() {
        Animation anim = new Animation("injector-on.png");
        //anim.setAngle(90);
        return anim;
    }

    @Override
    public Animation getOffAnimation() {
        Animation anim = new Animation("injector-off.png");
        //anim.setAngle(90);
        return anim;
    }

    @Override
    public void addItem(Item item) {
        if(ResourceCost.isInjectorMaterial(item.getItem())) {
            itemsToInject.add(item);
        }
        if (ResourceCost.isFuel(item.getItem())) {
            // absorb fuel as number
            // TODO, create table of fuel amounts per type of fuel (coal: 1,
            fuel += ResourceCost.getFuelAmount(item.getItem());
            super.addItem(item);
        } else {
            super.addItem(item);
        }
        startInjectingNextMaterialIfPossible();
    }

    public void startInjectingNextMaterialIfPossible() {
        if (fuel > 0 && !itemsToInject.isEmpty()) {
            fuel--;
            InjectorBlock injectorBlock = this;
            FXGL.getGameTimer().runOnceAfter(new Runnable() {
                @Override
                public void run() {
                    // produce mold part
                    Item item = itemsToInject.remove();
                    Type type = (Type) item.getItem().getType();
                    CastItem castItem = new CastItem(mold, type);
                    // add new item to block this is facing
                    Block neighbor = getNeighborBlock(0, -1, true);
                    Item createdItem = new Item(castItem, new IntLoc(0, 0), neighbor);
                    createdItem.setLayoutOffset(Rand.randomIntLoc(10, 2));
                    neighbor.addItem(createdItem);
                    // TODO, create entity without passing in coords
                    //castItem.getAnimation().createEntity(0, 0).setZ(neighbor.getZ() + 1);
                    neighbor.showItems();
                    getNeighbors().stream().filter(block -> block.getType().equals(Type.Computer))
                            .map(block -> (ComputerBlock) block).forEach(computerBlock -> {
                                computerBlock.itemCreated(injectorBlock, mold);
                    });
                }
            }, Duration.seconds(2));
        }
    }

}
