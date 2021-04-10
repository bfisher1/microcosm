package microcosm;

import lombok.Getter;
import lombok.Setter;

/**
 * Motor control variable, an easy way to store a value's goal, as well as its current value.
 * (i.e. a motor's current angle and it's goal)
 */
@Getter
@Setter
public class MotorControl {
    private double value;
    private double goal;
    private double rate;
    private double threshold;
    //private boolean changing;

    public MotorControl(double value, double goal, double rate, double threshold) {
        this.value = value;
        this.goal = goal;
        this.rate = rate;
        this.threshold = threshold;
        //this.changing = false;
    }

    public void increment() {
        this.value += rate;
    }

    public boolean atGoal() {
        return Math.abs(this.value - this.goal) <= this.threshold;
    }
}
