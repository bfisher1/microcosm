/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package microcosm;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import player.Camera;
import robot.Robot;
import util.IntLoc;
import world.Chunk;
import world.World;
import world.block.Block;
import world.block.BlockFactory;

/**
 * Shows how to use textures to draw entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GameApp extends GameApplication {

    private Camera camera;
    private World world;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {

        Galaxy galaxy = new Galaxy();


        world = new World(0, 0);
        camera = new Camera(0, 0, world);
        world.getCameras().add(camera);
        world.loadInitialChunks();;

        Robot robot = new Robot(0, 0);
        robot.setCurrentWorld(world);

        world.getCameras().add(camera);

        //world.replaceBlockAt(0, 0, BlockFactory.create(0, 0, Block.Type.Sand, world));

        galaxy.getWorlds().add(world);
        galaxy.addMob(robot);

        final int[] lastLoaded = {0};

        Runnable loader = new Runnable() {
            @Override
            public void run() {
                world.getAllBlocks();
                //world.move(6, 6);
            }
        };


        FXGL.getGameTimer().runAtInterval(loader, Duration.seconds(0));

        FXGL.getGameTimer().runAtInterval(new Runnable() {
            @Override
            public void run() {
                lastLoaded[0] -= 1;
                world.loadChunk(lastLoaded[0], lastLoaded[0]);
//                world.loadChunk(lastLoaded[0], lastLoaded[0] - 1);
//                world.loadChunk(lastLoaded[0] - 1, lastLoaded[0]);

            }
        }, Duration.seconds(1));



        FXGL.getGameTimer().runAtInterval(new Runnable() {
            @Override
            public void run() {
                robot.move();
                galaxy.runMobCollisions();

            }
        }, Duration.seconds(0));

        FXGL.getGameTimer().runAtInterval(new Runnable() {
            @Override
            public void run() {
                robot.setDirection(robot.getDirection() + Math.PI / 2.0);

            }
        }, Duration.seconds(2));


    }

    @Override
    protected void initInput() {
        Input input = FXGL.getInput();

        UserAction hitBall = new UserAction("Hit") {
            @Override
            protected void onActionBegin() {
                // action just started (key has just been pressed), play swinging animation
            }

            @Override
            protected void onAction() {
                // action continues (key is held), increase swing power
                camera.setX(camera.getX() + 1);
            }

            @Override
            protected void onActionEnd() {
                // action finished (key is released), play hitting animation based on swing power
            }
        };

        input.addAction(hitBall, KeyCode.F);
    }

    public static void main(String[] args) {
        launch(args);
    }
}