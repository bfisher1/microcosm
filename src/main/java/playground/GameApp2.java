package playground;

import animation.Animation;
import animation.AnimationBuilder;
import animation.Sprite;
import control.MouseControl;
import microcosm.KeyManager;
import util.PollingTimer;
import util.ScreenPlotter;
import world.block.Block;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.Timer;
import java.util.*;

/*
 * This applet allows the user to move a texture painted rectangle around the applet
 * window.  The rectangle flickers and draws slowly because this applet does not use
 * double buffering.
 */
public class GameApp2 {

    //TODO, move these to camera or config
    public static int WIDTH = 850;
    public static int HEIGHT = 850;
    public static int MIN_CAMERA_X = -50;
    public static int MIN_CAMERA_Y = -50;
    private static Canvas canvas;

    private static Map<Integer, Long> keyEventCounts = new HashMap();
    private static Map<Integer, Long> handledKeyEventCounts = new HashMap();


    public static void main(String[] args) {
        JFrame app = new JFrame();
        app.setIgnoreRepaint( true );
        app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        KeyManager keyManager = new KeyManager();
        app.addKeyListener(keyManager);


        canvas = new Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setSize( 840, 700 );

        app.add( canvas );
        app.pack();
        app.setVisible( true );

        canvas.createBufferStrategy( 2 );
        BufferStrategy buffer = canvas.getBufferStrategy();


        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int x = 3;
                x++;
                System.out.println("Mouse clicked");

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int x = 3;
                x++;
                System.out.println("Mouse pressed");
                if (mouseEvent.getClickCount() > 1) {
                    System.out.println("    2+ clicks!");
                }
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    recordMouseEvent(MouseControl.LEFT_PRESS);
                } else {
                    recordMouseEvent(MouseControl.RIGHT_PRESS);
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                int x = 3;
                x++;
                System.out.println("Mouse released");
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    recordMouseEvent(MouseControl.LEFT_RELEASE);
                } else {
                    recordMouseEvent(MouseControl.RIGHT_RELEASE);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {int x = 3;
                // when mouse enters screen
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                // when mouse leaves screen
            }
        });
        canvas.addKeyListener(keyManager);


        Timer timer = new Timer();
        List<World> worlds = new ArrayList<>();
//        worlds.add(new World(0, 40, 8));
//        worlds.add(new Sun(-24, 0, 8));
//        Sun sun = new Sun(38, 0, 5);
//        worlds.add(sun);
//
//        worlds.add(new World(0, 12, 30));
//        worlds.add(new World(0, -30, 8));

        worlds.add(new World(0, 0, 25));
        //worlds.add(new Sun(0, 0, 1, Block.Type.Uranium));
//        worlds.add(new Sun(4, 1, 1, Block.Type.Uranium));
//        worlds.add(new Sun(8, 1, 1, Block.Type.Sun));

        ScreenPlotter screenPlotter = new ScreenPlotter();
//        screenPlotter.plot(0, 0);
//        screenPlotter.plot(50, 50);
//        screenPlotter.plot(150, 50, 2000L);

        Graphics graphics = null;

        PollingTimer graphicsTimer = new PollingTimer(0);

        Animation background = AnimationBuilder.getBuilder()
                .fileName("purple-background.png")
                .zoomable(false)
                .build();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                worlds.stream().parallel().forEach(world -> {
                    recordPressedKeys();
                });
            }
        }, 0, 5);

//
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                worlds.stream().parallel().forEach(world -> {
//                    world.setOrientation(world.getOrientation() + .05 * 0);
//                });
//            }
//        }, 0, 15);

//
//        worlds.stream().forEach(world -> {
//            world.getBlockList().forEach(block -> {
//                Camera.getInstance().addSpriteToScreen(block.getSprite());
//            });
//        });

//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                new Thread(() -> worlds.parallelStream().forEach(world -> {
//                    world.getBlocks().forEach((loc, block) -> {
//                        block.updateTemperature();
//                        sun.setX(sun.getX() - .001);
//                    });
//                })).start();
//            }
//        }, 0, 1500);


//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                new Thread(() -> worlds.parallelStream().forEach(world -> {
//                    world.getBlocks().forEach((loc, block) -> {
//                        block.updateTemperature();
//                        sun.setX(sun.getX() - .001);
//                    });
//                })).start();
//            }
//        }, 0, 1500);

        while( true ) {
            try {
                if(graphicsTimer.resetIfReady()) {

                    /**
                     * Input list of worlds we are drawing
                     *
                     * For each world:
                     *      find screen position for center point of the world
                     *      use world size, rotation, and scale info to determine the bounding box of the visible world (top / bottom y and leftmost / rightmost x)
                     *      create buffered image for world
                     *      draw each block onto the world image
                     *      draw mobs and other sprites at respected locations on world
                     *      draw world image at world location
                     *
                     *      a fair bit of this could be parallelized
                     */


                    //updateWorldSprites(worlds);
                    handlePressedKeys(worlds);

                    background.draw(buffer.getDrawGraphics(), 0, 0);

                    if (Camera.getInstance().getLockedWorld() != null) {
                        Camera.getInstance().setX(Camera.getInstance().getLockedWorld().getX());
                        Camera.getInstance().setY(Camera.getInstance().getLockedWorld().getY());
                        Camera.getInstance().setOrientation(Camera.getInstance().getLockedWorld().getOrientation());
                    }

                    worlds.forEach(world -> {
                        runWorldLoop(world);
                        world.draw(buffer.getDrawGraphics(), new ScreenInfo(WIDTH, HEIGHT), Camera.getInstance());
//                        world.getVisibleBlocks().parallelStream().forEach(block -> {
//                            if (block.getCorners().isInside(getMouseLocation().x, getMouseLocation().y)) {
//                                screenPlotter.plot(block.getScreenLocation().getX(), block.getScreenLocation().getY(), true);
//                            }
//                        });
                    });


                    screenPlotter.draw(buffer.getDrawGraphics(), new ScreenInfo(WIDTH, HEIGHT), Camera.getInstance());


                    if (!buffer.contentsLost())
                        buffer.show();

                    Thread.yield();
                }
            } finally {
                if( graphics != null )
                    graphics.dispose();
            }
        }
    }

    private static void runWorldLoop(World world) {
        world.runExecutableBlocks();
        world.setOrientation(world.getOrientation() + world.getRotationSpeed());
//        world.setX(world.getX() + world.getVelocity().getX());
//        world.setY(world.getY() + world.getVelocity().getY());
        if (world instanceof Sun) {
            ((Sun) world).revolve();
        }
    }

    private static void handlePressedKeys(List<World> worlds) {
        double speed = -2.5 / 30.0; // * (1.0 - Camera.getInstance().getZoom());

        keyEventCounts.forEach((key, count) -> {
            Long handledCount = 0L;
            if (handledKeyEventCounts.containsKey(key)) {
                handledCount = handledKeyEventCounts.get(key);
            }
            Long unhandledCount = count - handledCount;
            if (unhandledCount > 0) {
                switch (key) {
                    case KeyEvent.VK_RIGHT:
                        Camera.getInstance().move(-speed * unhandledCount, 0);
                        break;
                    case KeyEvent.VK_LEFT:
                        Camera.getInstance().move(speed * unhandledCount, 0);
                        break;
                    case KeyEvent.VK_UP:
                        Camera.getInstance().move(0, speed * unhandledCount);
                        break;
                    case KeyEvent.VK_DOWN:
                        Camera.getInstance().move(0, -speed * unhandledCount);
                        break;
                    case KeyEvent.VK_CONTROL:
                        Camera.getInstance().zoomIn(unhandledCount);
                        break;
                    case KeyEvent.VK_SHIFT:
                        Camera.getInstance().zoomOut(unhandledCount);
                        break;
                    default:
                        break;
                }
                if (key == MouseControl.LEFT_PRESS) {
                    System.out.println("Handling left press");
                    worlds.parallelStream().forEach(world -> {
                        if (world.getHoveredBlock() != null) {
                            world.getHoveredBlock().setSelected(true);
                            Camera.getInstance().setLockedWorld(world);
                        }
                    });
                }
                if (key == MouseControl.RIGHT_PRESS) {
                    //
                }
                if (key == MouseControl.LEFT_RELEASE) {
                    //
                }
                if (key == MouseControl.RIGHT_RELEASE) {
                    //
                }
            }
            handledKeyEventCounts.put(key, count);
        });
    }

    public static Point getMouseLocation() {
        if (canvas == null || canvas.getMousePosition() == null) {
            return new Point(0, 0);
        }
        return canvas.getMousePosition();
    }

    private static void recordPressedKeys() {
        int[] keyEvents = { KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT };
        for(int i = 0; i < keyEvents.length; i++) {
            int key = keyEvents[i];
            if (KeyManager.isBeingPressed(key)) {
                if (!keyEventCounts.containsKey(key)) {
                    keyEventCounts.put(key, 1L);
                } else {
                    keyEventCounts.put(key, keyEventCounts.get(key) + 1);
                }
            }
        }
    }

    private static void recordMouseEvent(Integer mouseEvent) {
        if (!keyEventCounts.containsKey(mouseEvent)) {
            keyEventCounts.put(mouseEvent, 1L);
        } else {
            keyEventCounts.put(mouseEvent, keyEventCounts.get(mouseEvent) + 1);
        }
    }

    private static void updateWorldSprites(Collection<World> worlds) {
        worlds.forEach(world -> {
            world.getBlocks().forEach((location, block) -> {
               world.adjustSprite(block);
               if((block.getSprite().getX() < MIN_CAMERA_X || block.getSprite().getX() > WIDTH || block.getSprite().getY() < MIN_CAMERA_Y || block.getSprite().getY() > HEIGHT) || shouldNotBeVisible(block)) {
                   Camera.getInstance().removeSpriteFromScreen(block.getSprite());
               } else if (!Camera.getInstance().getSprites().contains(block.getSprite())) {
                   Camera.getInstance().addSpriteToScreen(block.getSprite());
               }
            });
            world.getMobs().forEach(mob -> {
                if(mob.readyToRun()) {
                    mob.runLogic();
                }
                world.adjustSprite(mob);
                // TODO, dry out this logic
                if(mob.getSprite().getX() < MIN_CAMERA_X || mob.getSprite().getX() > WIDTH || mob.getSprite().getY() < MIN_CAMERA_Y || mob.getSprite().getY() > HEIGHT) {
                    Camera.getInstance().removeSpriteFromScreen(mob.getSprite());
                } else if (!Camera.getInstance().getSprites().contains(mob.getSprite())) {
                    Camera.getInstance().addSpriteToScreen(mob.getSprite());
                }
            });

        });
    }

    private static boolean shouldNotBeVisible(Block block) {
        return  (block.blockAbove().isPresent() && !block.blockAbove().get().getType().equals(Block.Type.Tree)) && block.cardinalNeighbors().size() == 4;
    }

    private static void draw(Graphics graphics, Animation background) {


        background.draw(graphics, 0, 0);

        PriorityQueue<Sprite> sprites = new PriorityQueue<Sprite>(Camera.getInstance().getSprites());
        while(!sprites.isEmpty()) {
            try {
                sprites.remove().draw(graphics);
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }
    
}
