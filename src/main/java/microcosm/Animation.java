package microcosm;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import lombok.Getter;
import lombok.Setter;

/*
    Wrapper class around single frame anim and regular animation;
 */

@Getter
@Setter
public class Animation {
    private boolean isAnim;
    private double animDelay;
    private String animName;
    private int animFrames;
    private double scaleX;
    private double scaleY;


    public Animation(String animName, int frames, double delay, double scaleX, double scaleY) {
        //TODO refactor and don't use animDelay or animFrames, redundant
        setAnimName(animName);
        isAnim = true;
        animDelay = delay;
        animFrames = frames;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public Animation(String animName, int frames, double delay) {
        this(animName, frames, delay, 1.0, 1.0);
    }

    public Animation(String animName) {
        setAnimName(animName);
        isAnim = false;
        this.scaleX = 1.0;
        this.scaleY = 1.0;
    }



    public Entity createEntity(int x, int y) {
        if (isAnim) {
            return  FXGL.entityBuilder()
                    .at(x, y)
                    .scale(scaleX, scaleY)
                    .with(new AnimationClip(animName, animFrames, animDelay))
                    .buildAndAttach();
        } else {
            return FXGL.entityBuilder()
                    .at(x, y)
                    .scale(scaleX, scaleY)
                    .view(animName)
                    .buildAndAttach();
        }
    }

}
