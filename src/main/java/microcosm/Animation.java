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


    public Animation(String animName, int frames, double delay) {
        //TODO refactor and don't use animDelay or animFrames, redundant
        setAnimName(animName);
        isAnim = true;
        animDelay = delay;
        animFrames = frames;
    }

    public Animation(String animName) {
        setAnimName(animName);
        isAnim = false;
    }



    public Entity createEntity(int x, int y) {
        if (isAnim) {
            return  FXGL.entityBuilder()
                    .at(x, y)
                    .with(new AnimationClip(animName, animFrames, animDelay))
                    .buildAndAttach();
        } else {
            return FXGL.entityBuilder()
                    .at(x, y)
                    .view(animName)
                    .buildAndAttach();
        }
    }

}
