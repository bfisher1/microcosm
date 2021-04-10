package microcosm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryMotorControl {
    private boolean value;
    private boolean goal;
    private boolean changing;

    public BinaryMotorControl(boolean value, boolean goal) {
        this.value = value;
        this.goal = goal;
        this.changing = false;
    }
}
