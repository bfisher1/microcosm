/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;
import robot.Robot;
import world.Chunk;
import world.World;

/**
 * Shows how to use textures to draw entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GameApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {

        World world = new World(2315, 1231);

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

        Robot robot = new Robot(0, 0);


        FXGL.getGameTimer().runAtInterval(new Runnable() {
            @Override
            public void run() {
                robot.move();

            }
        }, Duration.seconds(0));

        FXGL.getGameTimer().runAtInterval(new Runnable() {
            @Override
            public void run() {
                robot.setDirection(robot.getDirection() + Math.PI / 2.0);

            }
        }, Duration.seconds(2));


    }

    public static void main(String[] args) {
        launch(args);
    }
}