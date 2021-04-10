package world.block;

import animation.Animation;
import animation.AnimationBuilder;
import machine.Arm;
import machine.ArmAction;
import playground.Camera;
import playground.World;
import util.IntLoc;
import world.block.execution.ConstantlyExecutable;

import java.awt.*;
import java.util.Queue;

public class ArmBlock extends ElectronicDevice implements ConstantlyExecutable {

    private Arm arm;


    @Override
    public void startExecuting() {
        arm.startExecuting();
        getWorld().startExecuting(getLocation(), this);
    }


    @Override
    public void execute() {
        arm.execute();
    }


    @Override
    public boolean shouldStopExecuting() {
        return arm.shouldStopExecuting();
    }



    public ArmBlock(int x, int y, World world) {
        super(x, y, world);
        this.setFullyCoveringView(false);
        setOn(false);

        arm = new Arm(this);
    }

    @Override
    public Animation getOnAnimation() {
        return AnimationBuilder.getBuilder().fileName("arm/base.png").build();
    }

    @Override
    public Animation getOffAnimation() {
        return AnimationBuilder.getBuilder().fileName("arm/base.png").build();
    }

    public void draw(Graphics2D graphics, IntLoc worldCenterScreenLoc) {
        super.draw(graphics, worldCenterScreenLoc);
        //drawArm(graphics, worldCenterScreenLoc);
    }

    public void drawArm(Graphics2D graphics, IntLoc worldCenterScreenLoc) {
        int xShift = Block.BLOCK_SCREEN_WIDTH / 2;
        arm.getArmAnimation().draw(graphics,
                (int) ((getX() - 1.60) * Block.BLOCK_SCREEN_WIDTH * Camera.getInstance().getZoom()),
                (int) ((getY() - 1.25) * Block.BLOCK_SCREEN_WIDTH * Camera.getInstance().getZoom()),
                (int) arm.getShoulderAngle().getValue(),
                arm.getArmAnimation().getCurrentFrame().getWidth() / 2,
                arm.getArmAnimation().getCurrentFrame().getHeight()
        );
    }

    public void drawItemsOnTopOf(Graphics2D graphics, IntLoc worldCenterScreenLoc) {
        super.drawItemsOnTopOf(graphics, worldCenterScreenLoc);
        drawArm(graphics, worldCenterScreenLoc);
    }

    public boolean hasSomethingOnTopOf() {
        return true;
    }

    public void beginSequence(Queue<ArmAction> actionSequence) {
        arm.beginSequence(actionSequence);
    }

    /**
     * Extend arm
     * Grab item
     * retract arm
     * rotate arm to face right block
     * release item
     * retract arm
     * rotate arm forward again
     *
     * Need fsm for executing steps
     */


}