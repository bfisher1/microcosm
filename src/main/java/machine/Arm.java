package machine;

import animation.Animation;
import animation.AnimationBuilder;
import lombok.Getter;
import lombok.Setter;
import microcosm.BinaryMotorControl;
import microcosm.MotorControl;
import playground.BlockLocation;
import playground.World;
import util.Loc;
import world.block.ArmBlock;
import world.block.Block;
import world.resource.Item;
import world.resource.WorldItem;
import world.resource.raw.IronRubble;
import world.resource.smelt.SmeltedIron;

import java.util.*;
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
    private Animation armScrewAnimation;
    private Animation armWeldAnimation;
    private Animation armStillOpenAnimation;
    private Animation armStillClosedAnimation;
    private Animation armRetractedAnimation;

    private Timer timer = new Timer();

    private ArmAction currentAction = new ArmAction(ArmActionType.Idle);

    private Queue<ArmAction> actionSequence = new ConcurrentLinkedQueue<>();

    private ArmBlock armBlock;

    private List<Item> itemsBeingHeld = new ArrayList();

    public Arm(ArmBlock armBlock) {
        this.armBlock = armBlock;
        shoulderAngle = new MotorControl(0.0, 0.0, 0.0, 1.0);
        clawOpened = new BinaryMotorControl(false, false);
        armExtended = new BinaryMotorControl(false, false);

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
                .fileName("arm/left-arm-extend-opened.png")
                .framesAndDelay(5, .2)
                .loop(false)
                .onAnimationFinished(() -> {
                    //armAnimation = armWorkAnimation;
                })
                .build();

        armScrewAnimation = AnimationBuilder.getBuilder()
                .fileName("arm/arm-screw.png")
                .framesAndDelay(6, .05)
                .build();

        armWeldAnimation = AnimationBuilder.getBuilder()
                .fileName("arm/arm-weld.png")
                .framesAndDelay(6, .05)
                .loop(true)
                .build();

        armStillOpenAnimation = AnimationBuilder.getBuilder()
                .fileName("arm/arm-still-open.png")
                .build();

        armStillClosedAnimation = AnimationBuilder.getBuilder()
                .fileName("arm/arm-still-closed.png")
                .build();

        armRetractedAnimation = AnimationBuilder.getBuilder()
                .fileName("arm/left-arm-retracted.png")
                .build();

        armAnimation = armRetractedAnimation;

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
                return;
            case Extend:
                this.armAnimation = this.armExtendAnimation;
                this.resetArmAnimation();
                this.armExtended.setGoal(currentAction.getArgValue("goal", Boolean.class));
                if (!this.armExtended.isGoal()) {
                    this.armAnimation.setAtEnd();
                    this.armAnimation.reverse();
                }
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
            case Screw:
                this.armAnimation = armScrewAnimation;
                this.resetArmAnimation();
                this.armAnimation.setLoopTimes(currentAction.getArgValue("times", Integer.class));
                this.armAnimation.setOnAnimationFinished(() -> {
                    this.armAnimation = armStillClosedAnimation;
                    switchToNextAction();
                });
                break;
            case Weld:
                this.armAnimation = armWeldAnimation;
                this.resetArmAnimation();
                this.armAnimation.setLoop(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        armAnimation = armStillOpenAnimation;
                        switchToNextAction();
                    }
                }, currentAction.getArgValue("duration", Long.class).longValue());
                break;
            case Grab:
                this.armAnimation = armStillOpenAnimation;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        armAnimation = armStillClosedAnimation;
                        Optional<Block> blockArmOver = getBlockArmIsAbove();
                        blockArmOver.ifPresent(block -> {
                            if (!block.getItemsOn().isEmpty()) {
                                WorldItem worldItem = block.getItemsOn().values().stream().findAny().get();
                                itemsBeingHeld.add(worldItem.getItem());
                                worldItem.delete();
                            }
                        });
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                switchToNextAction();
                            }
                        }, 500L);
                    }
                }, 500L);
                break;
            case Release:
                this.armAnimation = armStillOpenAnimation;
                this.itemsBeingHeld.stream().forEach(item -> {
                    getBlockArmIsAbove().ifPresent(block -> {
                        new WorldItem(item, block.getWorld(), block.getX() + .4, block.getY() + .4, block.getZ());
                    });
                });
                this.itemsBeingHeld = new ArrayList<>();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        switchToNextAction();
                    }
                }, 800L);
                break;
            case Wait:
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        switchToNextAction();
                    }
                }, currentAction.getArgValue("duration", Long.class).longValue());
                break;
        }
    }

    private Optional<Block> getBlockArmIsAbove() {
        double angle = shoulderAngle.getValue();
        if (-12 <= angle && angle <= 12) {
            return armBlock.blockRelative(0, -2, 0);
        }
        else if (16 <= angle && angle <= 35) {
            return armBlock.blockRelative(1, -2, 0);
        } else if (57 <= angle && angle <= 75) {
            return armBlock.blockRelative(2, -1, 0);
        } else if (82 <= angle && angle <= 105) {
            return armBlock.blockRelative(2, 0, 0);
        }
        return Optional.empty();
    }

    //next is going to be adding other actions
    // work action

    // then making meaningul sequences
    // then hooking up ability to move and drop items onto other blocks         (require arm holding screw driver)
    // and then to attatch items on the blocks                                  (require arm holding welder)

    // attach cases are
    // item1, item 2, screw item (screw action)
    // item 1, item 2 (weld)

    public Block.Direction directionArmFacing() {
        double angle = shoulderAngle.getValue();
        // maybe prevent bad values altogether
        if (angle <= 90) {
            return Block.Direction.Up;
        } else if (angle <= 180) {
            return Block.Direction.Right;
        } else if (angle <= 270) {
            return Block.Direction.Down;
        } else {
            return Block.Direction.Left;
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
