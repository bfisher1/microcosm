package machine;

import lombok.Getter;
import lombok.Setter;
import util.Loc;
import util.MathUtil;

@Getter
@Setter
public class LocMotorControl {
    private Loc value;
    private Loc goal;
    private double rate;
    private double threshold;

    public LocMotorControl(Loc value, Loc goal, double rate, double threshold) {
        this.value = value;
        this.goal = goal;
        this.rate = rate;
        this.threshold = threshold;
    }

    public void increment() {
        double angle = Math.atan2(goal.getY() - value.getY(), goal.getX() - value.getX());
        this.value.increase(new Loc(this.rate * Math.cos(angle), this.rate * Math.sin(angle)));
    }

    public boolean atGoal() {
        return MathUtil.dist(this.goal, this.value) <= this.threshold;
    }
}
