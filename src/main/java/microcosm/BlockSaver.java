package microcosm;

import lombok.AllArgsConstructor;
import util.DbClient;
import world.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@AllArgsConstructor
public class BlockSaver implements Runnable {

    public final static BlockingQueue<Block> blocks = new LinkedBlockingQueue<>();

    public final static int MAX_BLOCKS_TO_SAVE = 400;

    private boolean exitWhenSaved;

    public BlockSaver() {
        this(false);
    }

    public boolean allSaved() {
        return blocks.isEmpty();
    }

    public synchronized static void add(Block block) {
        try {
            blocks.put(block);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        blocks.notify();
    }

    public synchronized static void add(List<Block> blockList) {
        blockList.forEach(block -> {
            try {
                blocks.put(block);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        synchronized (blocks) {
            blocks.notify();
        }
    }

    @Override
    public void run() {
        while(true) {
            if (blocks.isEmpty()) {
                if (exitWhenSaved) {
                    System.out.println("Exiting after blocks queue has saved");
                    System.exit(0);
                }
                synchronized (blocks) {
                    try {
                        blocks.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                List<Block> blocksToSave = new ArrayList<>();
                for(int i = 0; i < MAX_BLOCKS_TO_SAVE; i++) {
                    if (blocks.isEmpty()) {
                        break;
                    }
                    try {
                        blocksToSave.add(blocks.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DbClient.saveBlocks(blocksToSave);
            }
        }
    }
}
