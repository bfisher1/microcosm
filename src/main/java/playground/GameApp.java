package playground;

import animation.Animation;
import animation.AnimationBuilder;
import animation.Sprite;
import microcosm.KeyManager;
import util.LazyTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;

/*
 * This applet allows the user to move a texture painted rectangle around the applet
 * window.  The rectangle flickers and draws slowly because this applet does not use
 * double buffering.
 */
public class GameApp {

    public static int WIDTH = 850;
    public static int HEIGHT = 850;
    public static int MIN_CAMERA_X = -50;
    public static int MIN_CAMERA_Y = -50;


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
        List<World> worlds = new ArrayList<>();
        worlds.add(new World(0, 0, 8));
        worlds.add(new Sun(12, 0, 8));
        worlds.add(new Sun(-12, 0, 8));

        worlds.add(new World(0, 12, 8));
        worlds.add(new World(0, -12, 8));


        Graphics graphics = null;

        LazyTimer keyTimer = new LazyTimer(2);
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


        worlds.stream().forEach(world -> {
            world.getBlocks().forEach(block -> {
                Camera.getInstance().addSpriteToScreen(block.getSprite());
            });
        });

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

    private static void handleKeys() {
        double speed = .5;
        if(KeyManager.isBeingPressed(KeyEvent.VK_RIGHT)) {
            Camera.getInstance().move(speed, 0);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_LEFT)) {
            Camera.getInstance().move(-speed, 0);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_UP)) {
            Camera.getInstance().move(0, -speed);
        }
        if(KeyManager.isBeingPressed(KeyEvent.VK_DOWN)) {
            Camera.getInstance().move(0, speed);
        }
    }

    private static void updateWorldSprites(Collection<World> worlds) {
        worlds.forEach(world -> {
            world.getBlocks().forEach(block -> {
               world.adjustSprite(block);
               if(block.getSprite().getX() < MIN_CAMERA_X || block.getSprite().getX() > WIDTH || block.getSprite().getY() < MIN_CAMERA_Y || block.getSprite().getY() > HEIGHT) {
                   Camera.getInstance().removeSpriteFromScreen(block.getSprite());
               } else if (!Camera.getInstance().getSprites().contains(block.getSprite())) {
                   Camera.getInstance().addSpriteToScreen(block.getSprite());
               }
            });
        });
    }

    private static void draw(Graphics graphics, Animation background) {

        background.draw(graphics, 0, 0);

        PriorityQueue<Sprite> sprites = new PriorityQueue<Sprite>(Camera.getInstance().getSprites());
        while(!sprites.isEmpty()) {
            sprites.remove().draw(graphics);
        }
    }
    
}
