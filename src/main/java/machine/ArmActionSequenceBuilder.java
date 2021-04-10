package machine;

import lombok.NoArgsConstructor;
import world.block.ArmBlock;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@NoArgsConstructor
public class ArmActionSequenceBuilder {
    private Queue<ArmAction> actions = new ConcurrentLinkedQueue<>();
    private ArmAction latestAction;

    public static ArmActionSequenceBuilder getBuilder() {
        return new ArmActionSequenceBuilder();
    }

    public ArmActionSequenceBuilder type(ArmActionType type) {
        latestAction = new ArmAction(type);
        actions.add(latestAction);
        return this;
    }

    public ArmActionSequenceBuilder arg(String arg, Object val) {
        latestAction.setArg(arg, val);
        return this;
    }

    public Queue<ArmAction> build() {
        return actions;
    }
}
