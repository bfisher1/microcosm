package microcosm;

import com.almasb.fxgl.dsl.FXGL;
import lombok.Getter;
import lombok.Setter;
import player.Camera;
import util.IntLoc;
import world.Chunk;
import world.World;
import world.block.Block;

import java.util.*;

@Getter
@Setter
public class ChunkLoader implements Runnable {

    private double lastX;
    private double lastY;
    private Camera camera;
    // world id
    private Map<Long, World> worlds;
    // world id
    private Map<Long, List<IntLoc>> allLocsToLoad;
    public static int LOAD_RADIUS = 2;

    public ChunkLoader(Camera camera, Map<Long, World> worlds) {
        this.camera = camera;
        this.worlds = worlds;
        lastX = camera.getX();
        lastY = camera.getY();
        allLocsToLoad = new HashMap<>();
    }

    @Override
    public void run() {

        worlds.forEach((id, world) -> {
            List<IntLoc> locsToLoad = null;
            if (allLocsToLoad.containsKey(id))
                locsToLoad = allLocsToLoad.get(id);
            if (locsToLoad == null || locsToLoad.size() == 0 || lastX != camera.getX() || lastY != camera.getY()) {
                locsToLoad = new ArrayList<>();


                // divide by block size
                IntLoc start = camera.startBlockLoc(world);
                IntLoc end = camera.endBlockLoc(world);

                start.setX(start.getX() / Chunk.CHUNK_SIZE);
                start.setY(start.getY() / Chunk.CHUNK_SIZE);

                end.setX(end.getX() / Chunk.CHUNK_SIZE);
                end.setY(end.getY() / Chunk.CHUNK_SIZE);


                for(int i = start.getX() - LOAD_RADIUS; i < end.getX() + LOAD_RADIUS; i++) {
                    for(int j = start.getY() - LOAD_RADIUS; j < end.getY() + LOAD_RADIUS; j++) {
                        IntLoc loc = new IntLoc(i, j);
                        if(!world.getChunks().containsKey(loc))
                            locsToLoad.add(loc);
                    }
                }
                locsToLoad.sort(new Comparator<IntLoc>() {
                    @Override
                    public int compare(IntLoc intLoc, IntLoc t1) {
                        double distance1 =  Math.sqrt(Math.pow((double) intLoc.getX() - lastX, 2) +  Math.pow((double) intLoc.getY() - lastY, 2));
                        double distance2 =  Math.sqrt(Math.pow((double) t1.getX() - lastX, 2) +  Math.pow((double) t1.getY() - lastY, 2));
                        return (int) (distance1 - distance2) / 10;
                    }
                });
            }

//        locsToLoad = new ArrayList<>();
//        IntLoc locToAdd = new IntLoc(1, 1);
//        locsToLoad.add(locToAdd);

            if(locsToLoad.size() > 0) {
                IntLoc loc = locsToLoad.remove(0);
                if (!world.getChunks().containsKey(loc)) {
                    Chunk chunk = new Chunk(loc.getX(), loc.getY(), world);
                    world.getChunks().put(loc, chunk);
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // System.out.println("Loading chunk " + chunk.getXId() + " " + chunk.getYId() );
                            chunk.load();
                        }
                    })).start();
                }
            }

            allLocsToLoad.put(id, locsToLoad);

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
        });



    }
}
