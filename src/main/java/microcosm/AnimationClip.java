package microcosm;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AnimationClip extends Component {

    private AnimatedTexture texture;
    private AnimationChannel anim;
    private String animName;
    private int frameNum;

    private static List<AnimationClip> newAnims = new ArrayList<>();

    public AnimationClip(String animName, int frameNum, Double seconds) {
        this.animName = animName;
        this.frameNum = frameNum;
        Image image = FXGL.image(animName);
        anim = new AnimationChannel(image, 1, (int) image.getWidth(), (int) image.getHeight() / frameNum, Duration.seconds(seconds), 0, frameNum - 1);

        texture = new AnimatedTexture(anim);

        // keep all anims in sync
        this.texture.setOnCycleFinished(new Runnable() {
            @Override
            public void run() {
                // should this be threadsafe? ... probably
                // is it needed for now? ... probably not
                List<Integer> animsToRemove = new ArrayList<>();
                // goal here is to make new anims play once they have been added to the screen
                for(int i = 0; i < newAnims.size(); i++) {
                    animsToRemove.add(i);
                    newAnims.get(i).texture.loop();
                }
                // remove the anims bit by bit to make sure no new items were added while this was running
                animsToRemove.forEach(index -> {
                    newAnims.remove(index);
                });
            }
        });
        newAnims.add(this);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
    }


}