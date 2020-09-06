package player;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import util.DbClient;

import java.util.stream.Collectors;


@Getter
@Setter
public class Player {

    private Camera camera;

    private int cameraSpeed = 5;

    public Player(Camera camera) {
        this.camera = camera;
    }

    public UserAction upArrow = new UserAction("Up") {
        @Override
        protected void onAction() {
            camera.setY(camera.getY() - cameraSpeed);
//            camera.getRenderedBlocks().forEach((world, locs) -> {
//                DbClient.saveList(locs.stream().map(loc -> world.getBlockAt(loc.getX(), loc.getY())).collect(Collectors.toList()));
//            });
        }
    };
//
//    public UserAction downArrow = new UserAction("Down") {
//        @Override
//        protected void onAction() {
//            camera.setY(camera.getY() + cameraSpeed);
//        }
//    };
//
//    public UserAction leftArrow = new UserAction("Left") {
//        @Override
//        protected void onAction() {
//            camera.setX(camera.getX() - cameraSpeed);
//        }
//    };
//
//    public UserAction rightArrow = new UserAction("Right") {
//        @Override
//        protected void onAction() {
//            camera.setX(camera.getX() + cameraSpeed);
//        }
//    };
//
//    public UserAction leftClick = new UserAction("LeftClick") {
//        @Override
//        protected void onAction() {
//            input.getMouseXWorld();
//            input.getMouseYWorld();
//        }
//    };
//
//    public void initializeInput() {
//        input.addAction(upArrow, KeyCode.UP);
//        input.addAction(downArrow, KeyCode.DOWN);
//        input.addAction(leftArrow, KeyCode.LEFT);
//        input.addAction(rightArrow, KeyCode.RIGHT);
//        input.addAction(leftClick, MouseButton.PRIMARY);
//    }
}
