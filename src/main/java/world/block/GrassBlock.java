package world.block;

import animation.AnimationBuilder;
import lombok.NoArgsConstructor;
import playground.Camera;
import playground.World;
import world.TemperatureState;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Optional;

@Entity
@Table(name = "grass_block")
@NoArgsConstructor
public class GrassBlock extends Block {
    public GrassBlock(int x, int y, World world) {
        super(x, y, world);
        state = State.Frozen;
        updateTemperature();
    }

    public enum State {
        Frozen,
        Normal,
        Scorched
    }

    private TemperatureState temperatureState;
    private State state;

    public void updateTemperature() {
        super.updateTemperature();
        //System.out.println("temperature " + getTemperature());
        
        if (getTemperature() < 45) {
            tryUpdateTemperatureState(TemperatureState.Cold);
        }
        else if (getTemperature() < 75) {
            tryUpdateTemperatureState(TemperatureState.Normal);
        }
        else if (getTemperature() < 100) {
            tryUpdateTemperatureState(TemperatureState.Hot);
        } else{
            getWorld().replaceBlockWithType(this, Type.Sand);
            Optional<Block> blockAbove = blockAbove();
            if (blockAbove.isPresent()) {
                if (blockAbove.get().getType().equals(Type.Tree)) {
                    ((TreeBlock) blockAbove.get()).setState(TreeBlock.State.Wilted);
                    blockAbove.get().setOnFire(true);
                }
            }
        }
    }

    private void tryUpdateTemperatureState(TemperatureState temperatureState) {
        if (this.temperatureState != temperatureState) {
            this.temperatureState = temperatureState;


            if (TemperatureState.Normal.equals(temperatureState) && !State.Scorched.equals(state)) {
                state = State.Normal;
            }
            else if (TemperatureState.Hot.equals(temperatureState)) {
                state = State.Scorched;
            }

            if (State.Frozen.equals(state) || state == null) {
                setAnimation("3d/frozen-grass.png");
            }
            else if (State.Normal.equals(state)) {
                setAnimation("3d/grass.png");
            }
            else if (State.Scorched.equals(state)) {
                setAnimation("3d/scorched-grass.png");
            }
        }
    }
}
