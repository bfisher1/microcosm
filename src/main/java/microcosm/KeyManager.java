package microcosm;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyManager implements KeyListener {

    public static Map<Integer, Boolean> pressedKeys = new HashMap<>();


    public static boolean isBeingPressed(int keyCode) {
        return pressedKeys.containsKey(keyCode) && pressedKeys.get(keyCode);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        pressedKeys.put(keyEvent.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        pressedKeys.put(keyEvent.getKeyCode(), false);
    }
}
