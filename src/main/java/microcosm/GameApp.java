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
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import player.Camera;
import player.Player;
import robot.Robot;
import util.IntLoc;
import world.Chunk;
import world.Sun;
import world.World;
import world.block.Block;
import world.block.BlockFactory;
import world.block.GeneratorBlock;

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
    private Sun sun;
    Player player;

    public static Texture water;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {

        water  = FXGL.getAssetLoader().loadTexture("water.png");

        Galaxy galaxy = new Galaxy();
        world = new World(0, 0);
        sun = new Sun(40 * Block.BLOCK_WIDTH, 10);



        camera = Camera.getInstance();
        camera.addWorldToRender(world);
        camera.addWorldToRender(sun);
        world.loadInitialChunks();
        sun.loadInitialChunks();

        Robot robot = new Robot(0, 0);
        robot.setCurrentWorld(world);

        robot.setCamera(camera);

        Player player = new Player(camera);
        player.initializeInput();


        //world.replaceBlockAt(0, 0, BlockFactory.create(0, 0, Block.Type.Sand, world));

        galaxy.getWorlds().add(world);
        galaxy.getWorlds().add(sun);
        galaxy.addMob(robot);

        ChunkLoader chunkLoader = new ChunkLoader(camera, world);
        ChunkLoader chunkLoader2 = new ChunkLoader(camera, sun);

        FXGL.getGameTimer().runAtInterval(chunkLoader, Duration.seconds(.1));
        FXGL.getGameTimer().runAtInterval(chunkLoader2, Duration.seconds(.1));



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
                world.getLoadedBlocksOfType(Block.Type.Generator).forEach(block -> {
                    GeneratorBlock generator = (GeneratorBlock) block;
                    generator.setOn(true);
                });
            }
        }, Duration.seconds(1.0));

        FXGL.getGameTimer().runAtInterval(new Runnable() {
            @Override
            public void run() {
                camera.removeBlocksThatShouldNotBeOnScreen();

                camera.renderBlocksThatShouldBeOnScreen();
            }
        }, new Duration(250));

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