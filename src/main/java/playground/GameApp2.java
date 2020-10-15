package playground;

import animation.Animation;
import animation.AnimationBuilder;
import animation.Sprite;
import microcosm.KeyManager;
import util.LazyTimer;
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

    public static int WIDTH = 850;
    public static int HEIGHT = 850;
    public static int MIN_CAMERA_X = -50;
    public static int MIN_CAMERA_Y = -50;
    private static Canvas canvas;


    public static void main(String[] args) {
        JFrame app = new JFrame();
        app.setIgnoreRepaint( true );
        app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        app.addKeyListener(new KeyManager());


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

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });


        Timer timer = new Timer();
        List<World> worlds = new ArrayList<>();
        worlds.add(new World(0, 40, 8));
        worlds.add(new Sun(-24, 0, 8));
        Sun sun = new Sun(38, 0, 5);
        worlds.add(sun);

        worlds.add(new World(0, 12, 30));
        worlds.add(new World(0, -30, 8));


        Graphics graphics = null;

        LazyTimer graphicsTimer = new LazyTimer(0);

        Animation background = AnimationBuilder.getBuilder().fileName("purple-background.png").build();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                worlds.stream().parallel().forEach(world -> {
                    handleKeys();
                });
            }
        }, 0, 5);

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

                    updateWorldSprites(worlds);
                    draw(buffer.getDrawGraphics(), background);

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

    public static Point getMouseLocation() {
        if (canvas == null || canvas.getMousePosition() == null) {
            return new Point(0, 0);
        }
        return canvas.getMousePosition();
    }

    private static void handleKeys() {
        double speed = .5;
        if(KeyManager.isBeingPressed(KeyEvent.VK_RIGHT)) {
            Camera.getInstance().move(-speed, 0);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_LEFT)) {
            Camera.getInstance().move(speed, 0);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_UP)) {
            Camera.getInstance().move(0, speed);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_DOWN)) {
            Camera.getInstance().move(0, -speed);
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
//            world.getMobs().forEach(mob -> {
//                if(mob.readyToRun()) {
//                    mob.runLogic();
//                }
//                world.adjustSprite(mob);
//                // TODO, dry out this logic
//                if(mob.getSprite().getX() < MIN_CAMERA_X || mob.getSprite().getX() > WIDTH || mob.getSprite().getY() < MIN_CAMERA_Y || mob.getSprite().getY() > HEIGHT) {
//                    Camera.getInstance().removeSpriteFromScreen(mob.getSprite());
//                } else if (!Camera.getInstance().getSprites().contains(mob.getSprite())) {
//                    Camera.getInstance().addSpriteToScreen(mob.getSprite());
//                }
//            });

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
