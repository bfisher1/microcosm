package machine;

import animation.Animation;
import animation.AnimationBuilder;
import lombok.Getter;
import lombok.Setter;
import microcosm.BinaryMotorControl;
import microcosm.MotorControl;
import world.block.ArmBlock;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@Setter
public class Arm {

    private BinaryMotorControl clawOpened;
    private MotorControl shoulderAngle;
    private BinaryMotorControl armExtended;
    private BinaryMotorControl working;

    private Animation armAnimation;
    private Animation armExtendAnimation;
    private Animation armWorkAnimation;

    private ArmAction currentAction = new ArmAction(ArmActionType.Idle);

    private Queue<ArmAction> actionSequence = new ConcurrentLinkedQueue<>();

    private ArmBlock armBlock;

    public Arm(ArmBlock armBlock) {
        this.armBlock = armBlock;
        shoulderAngle = new MotorControl(0.0, 0.0, 0.0, 1.0);
        clawOpened = new BinaryMotorControl(false, false);
        armExtended = new BinaryMotorControl(false, false);;

        armWorkAnimation = AnimationBuilder.getBuilder()
                .fileName("arm/left-arm-work.png")
                .framesAndDelay(3, .2)
                .loop(false)
                .onAnimationFinished(() -> {
                    //armAnimation.reverse();
                    //shoulderAngle.setValue(shoulderAngle.getValue() + 10);
                })
                .build();

        armExtendAnimation = AnimationBuilder.getBuilder()
                .fileName("arm/left-arm-extend.png")
                .framesAndDelay(6, .2)
                .loop(false)
                .onAnimationFinished(() -> {
                    //armAnimation = armWorkAnimation;
                })
                .build();

        armAnimation = armExtendAnimation;
    }

    public void startExecuting() {
        switch (currentAction.getType()) {
            case Idle:
                return;
            case Claw:
//                this.armAnimation = this.armExtendAnimation;
//                this.resetArmAnimation();
//                this.armAnimation.setOnAnimationFinished(() -> {
//                    this.armExtended.setGoal(currentAction.getArgValue("goal", Boolean.class));
//                    switchToNextAction();
//                });
            case Extend:
                this.armAnimation = this.armExtendAnimation;
                this.resetArmAnimation();
                this.armExtended.setGoal(currentAction.getArgValue("goal", Boolean.class));
                this.armExtended.setChanging(true);
                this.armAnimation.setOnAnimationFinished(() -> {
                    this.armExtended.setChanging(false);
                    this.armExtended.setValue(this.armExtended.isGoal());
                    switchToNextAction();
                });
                break;
            case Rotate:
                this.shoulderAngle.setRate(currentAction.getArgValue("rate", Double.class));
                this.shoulderAngle.setGoal(currentAction.getArgValue("goal", Double.class));
                break;
        }
    }


    private void resetArmAnimation() {
        this.armAnimation.setReverse(false);
        this.armAnimation.setLoop(false);
        this.armAnimation.reset();
    }

    public void execute() {
        switch (currentAction.getType()) {
            case Rotate:
                this.shoulderAngle.increment();
                if (this.shoulderAngle.atGoal()) {
                    switchToNextAction();
                }
                break;
        }
    }

    public boolean shouldStopExecuting() {
        return ArmActionType.Idle.equals(currentAction.getType());
    }

    private void switchToNextAction() {
        if (!this.actionSequence.isEmpty()) {
           this.currentAction = this.actionSequence.remove();
        } else {
            this.currentAction = new ArmAction(ArmActionType.Idle);
        }
        this.armBlock.startExecuting();
    }

    public void beginSequence(Queue<ArmAction> actionSequence) {
        this.actionSequence = actionSequence;
        switchToNextAction();
    }
}
