package animation;

public class AnimationBuilder {
    private Animation animation;


    public static AnimationBuilder getBuilder() {
        return new AnimationBuilder(new Animation());
    }

    public Animation build() {
        this.animation.load();
        return this.animation;
    }

    private AnimationBuilder(Animation animation) {
        this.animation = animation;
    }

    public AnimationBuilder fileName(String fileName) {
        getAnimation().setFilename(fileName);
        return this;
    }

    public AnimationBuilder framesAndDelay(int frames, double delay) {
        getAnimation().setFrames(frames);
        getAnimation().setDelay(delay);
        return this;
    }

    public AnimationBuilder scaleX(int scaleX) {
        getAnimation().setScaleX(scaleX);
        return this;
    }

    public AnimationBuilder scaleY(int scaleY) {
        getAnimation().setScaleY(scaleY);
        return this;
    }

    private Animation getAnimation() {
        if (animation == null) {
            animation = new Animation();
        }
        return animation;
    }

}
