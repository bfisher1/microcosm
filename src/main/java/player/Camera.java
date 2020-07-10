package player;

import com.almasb.fxgl.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import util.IntLoc;
import world.World;
import world.block.Block;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Camera {
    private int x;
    private int y;
    private World world;

    private Set<IntLoc> renderedBlocks;

    public Camera(int x, int y, World world) {
        this.x = x;
        this.y = y;
        this.world = world;
        this.renderedBlocks = new HashSet<>();
    }

    public void setX(int x) {
        this.x = x;
        updateVisibleBlocks();
    }

    public void setY(int y) {
        this.y = y;
        updateVisibleBlocks();
    }

    private void updateVisibleBlocks() {
        renderedBlocks.forEach(loc -> {
            if(world.isBlockLoaded(loc.getX(), loc.getY())) {
                Block block = world.getBlockAt(loc.getX(), loc.getY());
                if (block != null) {
                    Entity entity = block.getEntity();
                    if (entity != null) {
                        entity.setX(world.getX() + loc.getX() * block.BLOCK_WIDTH - x);
                        entity.setY(world.getY() + loc.getY() * block.BLOCK_WIDTH - y);
                    }
                }
            }
        });
    }

    public void addRenderedBlock(Block block) {
        renderedBlocks.add(block.getIntLoc());
    }

    public void removeRenderedBlock(Block block) {
        renderedBlocks.remove(block.getIntLoc());
    }
}
