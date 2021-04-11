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
    private Double min = 0.0;
    private Double max = 360.0;
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
        if (this.max != null && this.value >= this.max) {
            this.value -= this.max;
        }
        if (this.min != null && this.value < this.min) {
            this.value += this.max;
        }
    }

    public boolean atGoal() {
        return Math.abs(this.value - this.goal) <= this.threshold;
    }
}
