package microcosm;

import animation.Animation;
import animation.AnimationBuilder;
import animation.Sprite;
import item.Item;
import player.Camera;
import player.Player;
import robot.Robot;
import util.DbClient;
import util.IntLoc;
import util.LazyTimer;
import world.World;
import world.WorldFactory;
import world.block.Block;
import world.block.BlockFactory;
import world.block.GeneratorBlock;
import world.block.TreadmillBlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.*;

/*
 * This applet allows the user to move a texture painted rectangle around the applet
 * window.  The rectangle flickers and draws slowly because this applet does not use
 * double buffering.
 */
public class GameApp {


    static Camera camera;
    static Map<Long, World> worlds;

    public static void main(String[] args) {
        JFrame app = new JFrame();
        app.setIgnoreRepaint( true );
        app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        app.addKeyListener(new KeyManager());


        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setSize( 840, 700 );

        app.add( canvas );
        app.pack();
        app.setVisible( true );

        canvas.createBufferStrategy( 2 );
        BufferStrategy buffer = canvas.getBufferStrategy();


        Timer timer = new Timer();
        initGame(timer);

        Graphics graphics = null;

        LazyTimer keyTimer = new LazyTimer(2);
        LazyTimer graphicsTimer = new LazyTimer(0);

        Animation background = AnimationBuilder.getBuilder().fileName("purple-background.png").build();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handleKeys();
            }
        }, 0, 1);


        while( true ) {
            try {

//                if (keyTimer.resetIfReady()) {
//                    handleKeys();
//                }

                if(graphicsTimer.resetIfReady()) {
                    graphics = buffer.getDrawGraphics();

                    draw(graphics, background);

                    if (!buffer.contentsLost())
                        buffer.show();

//                    if(Camera.getInstance().getSprites().size() < 200)
//                        System.out.println("Sprite nums " + Camera.getInstance().getSprites().size());

                    Thread.yield();
                }
            } finally {
                if( graphics != null )
                    graphics.dispose();
            }
        }


    }

    private static void handleKeys() {
        if(KeyManager.isBeingPressed(KeyEvent.VK_RIGHT)) {
            Camera.getInstance().move(1, 0);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_LEFT)) {
            Camera.getInstance().move(-1, 0);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_UP)) {
            Camera.getInstance().move(0, -1);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_DOWN)) {
            Camera.getInstance().move(0, 1);
        }
    }

    private static void draw(Graphics graphics, Animation background) {

        Camera camera = Camera.getInstance();
        background.draw(graphics, 0, 0);
        Camera.spriteLock.lock();
        PriorityQueue<Sprite> sprites = new PriorityQueue<>(camera.getPrevSprites());
        Camera.spriteLock.unlock();
        try {
            while (!sprites.isEmpty()) {
                Sprite sprite = sprites.remove();
                sprite.draw(graphics);
            }
        } catch(Exception e) {
            System.out.println("error drawing" + e);
        }
    }


    private static void initGame(Timer timer) {

        // thread to save blocks to DB
        (new Thread(new BlockSaver(false))).start();

        Collection<World> dbWorlds = DbClient.findAll(World.class);

        worlds = new HashMap<>();
        dbWorlds.forEach(dbWorld -> {
            worlds.put(dbWorld.getId(), WorldFactory.create(dbWorld));
        });

        World.loadedWorlds = new ArrayList<>(worlds.values());


        Galaxy galaxy = new Galaxy();
        camera = Camera.getInstance();
        worlds.forEach((id, world) -> {
            camera.addWorldToRender(world);
            world.loadInitialChunks();
            galaxy.getWorlds().add(world);
        });

        World world = worlds.values().stream().filter(wrld -> wrld.getType() == World.Type.World).findFirst().get();


        timer.scheduleAtFixedRate(
                new TimerTask() {
                       @Override
                       public void run() {
                           spawnResourcesOnTreadmill(world);
                       }
                   }, 0, 2000);


        // robot
        robot.Robot robot = new Robot(0, 0);
        robot.setCurrentWorld(world);
        robot.setCamera(camera);
        galaxy.addMob(robot);

        Player player = new Player(camera);
        //player.initializeInput();


        ChunkLoader chunkLoader = new ChunkLoader(camera, worlds);

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        chunkLoader.run();
                    }
                }, 0, 100);

        // ASYNC VERSION
//        FXGL.getGameTimer().runAtInterval(new Runnable() {
//            @Override
//            public void run() {
//                (new Thread(chunkLoader)).start();
//            }
//        }, Duration.seconds(.1));


        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        robot.move();
                        galaxy.runMobCollisions();

                        world.getBlocksByType().get(Block.Type.Treadmill).forEach(block -> {
                            TreadmillBlock treadmillBlock = (TreadmillBlock) block;
                            if(treadmillBlock.isOn())
                                treadmillBlock.whileOn();
                        });

                        Camera.getInstance().updateVisibleBlocks();
                    }
                }, 0, 1);


        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        world.getLoadedBlocksOfType(Block.Type.Generator).forEach(block -> {
                            GeneratorBlock generator = (GeneratorBlock) block;
                            if(!generator.isOn())
                                generator.setOn(true);
                        });
                    }
                }, 0, 1000);


        new Thread(new Runnable() {
            @Override
            public void run() {
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        worlds.forEach((id, world) -> {
                            world.updateBlockTemperatures();
                        });
                    }
                }, 0, 1000);
            }
        }).start();


        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        camera.removeBlocksThatShouldNotBeOnScreen();

                        camera.renderBlocksThatShouldBeOnScreen();
                    }
                }, 0, 250);

        robot.setDirection(Math.PI / 2.0);
    }

    private static void spawnResourcesOnTreadmill(World world) {
        TreadmillBlock treadmillBlock = (TreadmillBlock) world.getBlockAt(0, 6, 2);
        Item silicon = new Item(BlockFactory.create(0, 0, Block.Type.Silicon, world), new IntLoc(8, 8), treadmillBlock);
        treadmillBlock.addItem(silicon); //layout offset of 8 to center it
        Item iron = new Item(BlockFactory.create(0, 0, Block.Type.Iron, world), new IntLoc(2, 8), treadmillBlock);
        treadmillBlock.addItem(iron);
        Item coal = new Item(BlockFactory.create(0, 0, Block.Type.Coal, world), new IntLoc(2, 2), treadmillBlock);
        treadmillBlock.addItem(coal);
        treadmillBlock.showItems();
    }

}
