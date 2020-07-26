package microcosm;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;
import player.Camera;
import player.Player;
import robot.Robot;
import util.DbClient;
import world.Sun;
import world.World;
import world.WorldFactory;
import world.block.Block;
import world.block.GeneratorBlock;

import java.util.*;

public class GameApp extends GameApplication {

    private Camera camera;
    private Map<Long, World> worlds;
    //private Sun sun;


    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {

        Collection<World> dbWorlds = DbClient.findAll(World.class);

        worlds = new HashMap<>();
        dbWorlds.forEach(dbWorld -> {
            worlds.put(dbWorld.getId(), WorldFactory.create(dbWorld));
        });


        Galaxy galaxy = new Galaxy();
        camera = Camera.getInstance();
        worlds.forEach((id, world) -> {
            camera.addWorldToRender(world);
            world.loadInitialChunks();
            galaxy.getWorlds().add(world);
        });

        World world = worlds.values().stream().filter(wrld -> wrld.getType() == World.Type.World).findFirst().get();

        // robot
        Robot robot = new Robot(0, 0);
        robot.setCurrentWorld(world);
        robot.setCamera(camera);
        galaxy.addMob(robot);

        Player player = new Player(camera);
        player.initializeInput();


        ChunkLoader chunkLoader = new ChunkLoader(camera, worlds);

        FXGL.getGameTimer().runAtInterval(chunkLoader, Duration.seconds(.1));



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