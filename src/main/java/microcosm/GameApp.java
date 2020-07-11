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
import player.Player;
import robot.Robot;
import util.IntLoc;
import world.Chunk;
import world.World;
import world.block.Block;
import world.block.BlockFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows how to use textures to draw entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GameApp extends GameApplication {

    private Camera camera;
    private World world;
    Player player;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {

        Galaxy galaxy = new Galaxy();
        world = new World(0, 0);


        camera = new Camera(0, 0, world);
        world.getCameras().add(camera);
        world.loadInitialChunks();;

        Robot robot = new Robot(60, 60);
        robot.setCurrentWorld(world);

        world.getCameras().add(camera);
        robot.setCamera(camera);

        Player player = new Player(camera);
        player.initializeInput();


        //world.replaceBlockAt(0, 0, BlockFactory.create(0, 0, Block.Type.Sand, world));

        galaxy.getWorlds().add(world);
        galaxy.addMob(robot);

        //ChunkLoader chunkLoader = new ChunkLoader(camera, world);

        //FXGL.getGameTimer().runAtInterval(chunkLoader, Duration.seconds(.1));



        FXGL.getGameTimer().runAtInterval(new Runnable() {
            @Override
            public void run() {
                robot.move();
                galaxy.runMobCollisions();
                List<Block> blocksToRemove= new ArrayList<>();
                camera.getRenderedBlocks().forEach(loc -> {
                    if(world.isBlockLoaded(loc.getX(), loc.getY())) {
                        Block block = world.getBlockAt(loc.getX(), loc.getY());
                        if (!block.onScreen()) {
                            blocksToRemove.add(block);
                        }
                    }
                });
                blocksToRemove.forEach(block -> {
                    block.removeFromScreen();
                });

                //TODO find chunks near camera center and load if not onscreen and should be

            }
        }, Duration.seconds(0));

        robot.setDirection(Math.PI / 2.0);
//
//        FXGL.getGameTimer().runAtInterval(new Runnable() {
//            @Override
//            public void run() {
//                robot.setDirection(Math.PI / 2.0);
//
//            }
//        }, Duration.seconds(2));


    }

    @Override
    protected void initInput() {
        //
    }

    public static void main(String[] args) {
        launch(args);
    }
}