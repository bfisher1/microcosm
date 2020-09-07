package item;


import animation.Animation;
import animation.Sprite;

public interface Itemable {
    Animation getAnimation();
    Object getType();
    Sprite getSprite();
    void setSprite(Sprite sprite);
}
