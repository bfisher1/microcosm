package world.block.execution;

/**
 * For blocks that need to execute each cycle of the game loop (i.e. treadmill always pushing items on top of it)
 */
public interface ConstantlyExecutable {
    void startExecuting();

    void execute();

    boolean shouldStopExecuting();
}
