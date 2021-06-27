package world.block;

import animation.Animation;
import animation.AnimationBuilder;
import lombok.Getter;
import lombok.Setter;
import machine.Arm;
import machine.ArmAction;
import machine.ArmActionSequenceBuilder;
import machine.ArmActionType;
import playground.BlockLocation;
import playground.Camera;
import playground.World;
import util.IntLoc;
import util.Loc;
import world.block.execution.ConstantlyExecutable;
import world.resource.assembly.AssemblyRequest;
import world.resource.assembly.AssemblyWork;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@Setter
public class ArmBlock extends ElectronicDevice implements ConstantlyExecutable {

    private Arm arm;

    private Queue<AssemblyRequest> assemblyRequests = new ConcurrentLinkedQueue<>();

    private boolean workingOnAssembly = false;

    private Set<BlockLocation> trayBlocks = new HashSet<>();

    private BlockLocation assemblyTray = null;

    private AssemblyRequest currentRequest = null;

    @Override
    public void startExecuting() {
        arm.startExecuting();
        getWorld().startExecuting(getLocation(), this);
    }

    public void makeRequest(AssemblyRequest request) {
        // todo remove hack
//        request.setInputItemTypeQuantities(new HashMap<>());
//        request.getInputItemTypeQuantities().put("PRINTED_PANEL", 1);
//        request.getInputItemTypeQuantities().put("PRINTED_GEAR", 1);
        assemblyRequests.add(request);
        startNextRequest();
    }

    public void requestFinished() {
        workingOnAssembly = false;
        // if all actions are finished, then the request has finished
        // now the outputs have been generated, so tell the assembly tray to delete inputs and create outputs
        getWorld().getBlockAt(assemblyTray).ifPresent(trayBlock -> {
            trayBlock.replaceInputItemsWithAssembledItem(currentRequest);
        });
        currentRequest = null;
        startNextRequest();
    }

    public void startNextRequest() {
        if (!workingOnAssembly) {
            if (!assemblyRequests.isEmpty()) {
                AssemblyRequest request = assemblyRequests.peek();

                // todo remove
                if (assemblyTrayHaveAllInputs(request)) {
                    // generate actions to move all inputs to assembly tray

                    ArmActionSequenceBuilder sequence = ArmActionSequenceBuilder.getBuilder()
                            .type(ArmActionType.Extend)
                            .arg("goal", true);

                    // face that block
                    Direction direction = getDirectionOfOtherBlock(assemblyTray);
                    // grab item of that type
                    sequence.type(ArmActionType.Face)
                            .arg("rate", .7)
                            .arg("direction", direction);

                    // generate welding actions
                    request.getAssemblyWork().stream().forEach(assemblyWork -> {
                        switch (assemblyWork.getAssemblyAction()) {
                            case Weld:
                                sequence.type(ArmActionType.Weld)
                                        .arg("duration", assemblyWork.getDuration());
                                break;
                            case Screw:
                                sequence.type(ArmActionType.Screw)
                                        .arg("times", assemblyWork.getTimes());
                                break;
                        }
                    });

                    // remove request from queue and begin sequence
                    currentRequest = assemblyRequests.remove();
                    arm.resetActionSequence(request, sequence);
                }
            }
        }
    }

    // todo, maybe it's just best to have a tray and to put all items on it,
    // then weld all them together

    private boolean assemblyTrayHaveAllInputs(AssemblyRequest request) {
        Map<String, Integer> itemTypeQuantitiesAcrossTrays = new HashMap<>();

        getWorld().getBlockAt(assemblyTray).ifPresent(block -> {
            block.getItemsOn().values().stream().forEach(worldItem -> {
                if (!itemTypeQuantitiesAcrossTrays.containsKey(worldItem.getItem().getType())) {
                    itemTypeQuantitiesAcrossTrays.put(worldItem.getItem().getType(), 0);
                }
                itemTypeQuantitiesAcrossTrays.put(worldItem.getItem().getType(), itemTypeQuantitiesAcrossTrays.get(worldItem.getItem().getType()) + (int) worldItem.getItem().getQuantity());
            });
        });

        return request.getInputItemTypeQuantities().keySet().stream().allMatch(inputType ->
            itemTypeQuantitiesAcrossTrays.containsKey(inputType) &&
            itemTypeQuantitiesAcrossTrays.get(inputType) >= request.getInputItemTypeQuantities().get(inputType)
        );
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

    public void scanForTrays() {
//        for (int x = -1; x <= 1; x++) {
//            for (int y = -1; y <= 1; y++) {
//                blockRelative(x, y, 0).ifPresent(block -> {
//                    if (block instanceof TrayBlock) {
//                        addBlockReference(block.getLocation());
//                        if (((TrayBlock) block).isForAssembly()) {
//                            this.assemblyTray = block.getLocation();
//                        } else {
//                            this.trayBlocks.add(block.getLocation());
//                        }
//                    }
//                });
//            }
//        }

        // for now, just look at the block above when scanning for neighboring trays
        blockRelative(0, -1, 0).ifPresent(block -> {
            if (block instanceof TrayBlock) {
                addBlockReference(block.getLocation());
                if (((TrayBlock) block).isForAssembly()) {
                    this.assemblyTray = block.getLocation();
                } else {
                    this.trayBlocks.add(block.getLocation());
                }
            }
        });
    }

    public void onReferencedBlockRemoved(BlockLocation blockLocation) {
        super.onReferencedBlockRemoved(blockLocation);
        if (trayBlocks.contains(blockLocation)) {
            trayBlocks.remove(blockLocation);
        }
        else if (blockLocation.equals(assemblyTray)) {
            assemblyTray = null;
        }
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
                (int) ((getX() - 1.60 + arm.getArmOriginPositionInBlock().getValue().getX()) * Block.BLOCK_SCREEN_WIDTH * Camera.getInstance().getZoom()),
                (int) ((getY() - 1.5  + arm.getArmOriginPositionInBlock().getValue().getY()) * Block.BLOCK_SCREEN_WIDTH * Camera.getInstance().getZoom()),
                (int) arm.getShoulderAngle().getValue(),
                arm.getArmAnimation().getCurrentFrame().getWidth() / 2,
                arm.getArmAnimation().getCurrentFrame().getHeight()
        );


        arm.getItemsBeingHeld().forEach(item -> {
            double radius = 1.8; // the arm's length is just under 2 blocks
            double angle = Math.toRadians(arm.getShoulderAngle().getValue() - 90);
            Loc loc = new Loc(getX() + radius * Math.cos(angle), getY() + radius * Math.sin(angle));

            item.getAnimation().draw(graphics,
                    (int) ((getX() + .5 + radius * Math.cos(angle) + arm.getArmOriginPositionInBlock().getValue().getX()) * Block.BLOCK_SCREEN_WIDTH * Camera.getInstance().getZoom()),
                    (int) ((getY() + .5 + radius * Math.sin(angle) + arm.getArmOriginPositionInBlock().getValue().getY()) * Block.BLOCK_SCREEN_WIDTH * Camera.getInstance().getZoom())
//                    (int) arm.getShoulderAngle().getValue(),
//                    arm.getArmAnimation().getCurrentFrame().getWidth() / 2,
//                    arm.getArmAnimation().getCurrentFrame().getHeight() / 2
            );
        });

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

}
