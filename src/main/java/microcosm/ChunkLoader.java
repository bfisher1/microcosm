package microcosm;

import lombok.Getter;
import lombok.Setter;
import player.Camera;
import util.IntLoc;
import world.Chunk;
import world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class ChunkLoader implements Runnable {

    private double lastX;
    private double lastY;
    private Camera camera;
    private World world;
    private List<IntLoc> locsToLoad;
    public static int LOAD_RADIUS = 2;

    public ChunkLoader(Camera camera, World world) {
        this.camera = camera;
        this.world = world;
        lastX = camera.getX();
        lastY = camera.getY();
        locsToLoad = null;
    }

    @Override
    public void run() {


        if (locsToLoad == null || locsToLoad.size() == 0 || lastX != camera.getX() || lastY != camera.getY()) {
            locsToLoad = new ArrayList<>();
            for(int i = -LOAD_RADIUS; i < LOAD_RADIUS; i++) {
                for(int j = -LOAD_RADIUS; j < LOAD_RADIUS; j++) {
                    locsToLoad.add(new IntLoc(i, j));
                }
            }
//            locsToLoad.sort(new Comparator<IntLoc>() {
//                @Override
//                public int compare(IntLoc intLoc, IntLoc t1) {
//                    double distance1 =  Math.sqrt(Math.pow((double) intLoc.getX() - lastX, 2) +  Math.pow((double) intLoc.getY() - lastY, 2));
//                    double distance2 =  Math.sqrt(Math.pow((double) t1.getX() - lastX, 2) +  Math.pow((double) t1.getY() - lastY, 2));
//                    return (int) (distance1 - distance2);
//                }
//            });
        }

//        locsToLoad = new ArrayList<>();
//        IntLoc locToAdd = new IntLoc(1, 1);
//        locsToLoad.add(locToAdd);

        IntLoc loc = locsToLoad.remove(0);
        if (!world.getChunks().containsKey(loc)) {
            Chunk chunk = new Chunk(loc.getX(), loc.getY(), world);
            world.getChunks().put(loc, chunk);
            chunk.load();;
        }

//        for(int i = 0; i < 10; i++) {
//            for(int j = 0; j < 10; j++) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if(i == 0 && j == 0)
//                    break;
//                (new Chunk(i, j)).load();
//
//            }
//        }

    }
}
