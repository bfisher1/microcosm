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

    public AnimationBuilder angle(int angle) {
        getAnimation().setAngle(angle);
        return this;
    }

    public AnimationBuilder animation(Animation animation) {
        this.animation = animation;
        return this;
    }

    public AnimationBuilder framesAndDelay(int frames, double delay) {
        getAnimation().setFrames(frames);
        getAnimation().setDelay(delay);
        return this;
    }

    public AnimationBuilder framesAndDelay(int horizontalFrames, int verticalFrames, double delay) {
        getAnimation().setHorizontalFrames(horizontalFrames);
        getAnimation().setVerticalFrames(verticalFrames);
        getAnimation().setFrames(horizontalFrames * verticalFrames);
        getAnimation().setDelay(delay);
        return this;
    }

    public AnimationBuilder scaleX(double scaleX) {
        getAnimation().setScaleX(scaleX);
        return this;
    }

    public AnimationBuilder scaleY(double scaleY) {
        getAnimation().setScaleY(scaleY);
        return this;
    }

    public AnimationBuilder xOffset(int xOffset) {
        getAnimation().setXOffset(xOffset);
        return this;
    }

    public AnimationBuilder yOffset(int yOffset) {
        getAnimation().setYOffset(yOffset);
        return this;
    }

    public AnimationBuilder zoomable(boolean zoomable) {
        getAnimation().setZoomable(zoomable);
        return this;
    }

    public AnimationBuilder sharedKey(String sharedKey) {
        getAnimation().setSharedKey(sharedKey);
        return this;
    }

    private Animation getAnimation() {
        if (animation == null) {
            animation = new Animation();
        }
        return animation;
    }

}
