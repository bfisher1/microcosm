package animation;

import kotlin.jvm.internal.Lambda;
import lombok.Getter;
import lombok.Setter;
import playground.Camera;
import playground.GameApp;
import util.MathUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
public class Animation {
    private String filename;
    private List<BufferedImage> animations;
    private double scaleX;
    private double scaleY;
    private int xOffset;
    private int yOffset;
    private double delay;
    private long lastFrameChange;
    private int animIndex;
    private boolean loop = true;
    private boolean reverse = false;
    private Runnable onAnimationFinished = null;

    /**
     * Number of frames in the animation. If using a spreadsheet, specify horizontal and vertical frames. Otherwise, just use frames.
     */
    private int frames;
    private Integer horizontalFrames;
    private Integer verticalFrames;

    private boolean zoomable = true;
    private int angle = 0;

    private String sharedKey = null;
    private static long lastSharedFrameChange;
    public static Map<String, List<Animation>> shared = new HashMap<>();

    public Animation() {
        animations = new ArrayList<>();
        lastFrameChange = System.currentTimeMillis();
        animIndex = 0;
        frames = 1;
        scaleX = 1.0;
        scaleY = 1.0;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
        if (sharedKey == null) {
            return;
        }
        if(!Animation.shared.containsKey(sharedKey)) {
            Animation.shared.put(sharedKey, new ArrayList<>());
        }
        Animation.shared.get(sharedKey).add(this);
    }

    public void setFilename(String fileName) {
        this.filename = "src/main/resources/assets/textures/" + fileName;
    }

    public void load() {
        BufferedImage bufferedImage;
        animations = new ArrayList<>();
        try {
            int width, height;

            String fileNameAtAngle = filename + '-' + angle;
            if(ImageBank.images.containsKey(fileNameAtAngle)) {
                bufferedImage = ImageBank.images.get(fileNameAtAngle);
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
            } else {
                bufferedImage = ImageIO.read(new File(filename));
                width = bufferedImage.getWidth(null);
                height = bufferedImage.getHeight(null);
                if (bufferedImage.getType() != BufferedImage.TYPE_INT_ARGB) {
                    BufferedImage bi2 =
                            new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics big = bi2.getGraphics();
                    big.drawImage(bufferedImage, 0, 0, null);
                    bufferedImage = bi2;
                }

                if (angle != 0) {
                    // todo, multiple could probably be less than 2
                    BufferedImage rotated = new BufferedImage(width * 2, height * 2, bufferedImage.getType());
                    Graphics2D graphic = rotated.createGraphics();
                    graphic.rotate(Math.toRadians(angle), width/2, height/2);
                    graphic.drawImage(bufferedImage, null, 0, 0);
                    graphic.dispose();
                    bufferedImage = rotated;
                }

                ImageBank.images.put(fileNameAtAngle, bufferedImage);
            }

            if (horizontalFrames != null && verticalFrames != null) {

                int frameHeight = height / verticalFrames;
                int frameWidth = width / horizontalFrames;

                for (int y = 0; y < verticalFrames; y++) {
                    for (int x = 0; x < horizontalFrames; x++) {
                        BufferedImage subImage = bufferedImage.getSubimage(x * frameWidth, y * frameHeight, frameWidth, frameHeight);
                        animations.add(subImage);
                    }
                }
            } else {
                int frameHeight = height / frames;

                for(int i = 0; i < frames; i++) {
                    BufferedImage subImage = bufferedImage.getSubimage(0, i * frameHeight, width, frameHeight);
                    animations.add(subImage);
                }
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Image could not be read");
        }
    }

    public void reset() {
        this.animIndex = 0;
    }

    public void reverse() {
        this.reverse = !this.reverse;
    }

    public boolean timeToChangeFrame() {
        if ( ((animIndex >= animations.size() - 1 && !reverse) || (animIndex <= 0 && reverse)) && !loop) {
            if (onAnimationFinished != null) {
                onAnimationFinished.run();
                onAnimationFinished = null;
            }
            return false;
        }
        // convert time difference to seconds
        return System.currentTimeMillis() - lastFrameChange >= delay * 1000;
    }

    public boolean timeToChangeSharedFrame() {
        // convert time difference to seconds
        return System.currentTimeMillis() - lastSharedFrameChange >= delay * 1000;
    }

    public void draw(Graphics g, int x, int y) {
        draw(g, x, y, 0, null, null);
    }

    public void draw(Graphics g, int x, int y, Integer angle, Integer rotationOriginX, Integer rotationOriginY) {
        x += xOffset;
        y += yOffset;

        // draw, may want to pass graphics here
        // or have graphics as static
        if (frames > 1) {
            if (sharedKey == null) {
                if (timeToChangeFrame()) {
                    lastFrameChange = System.currentTimeMillis();
                    incrementAnimIndex();
                }
            } else {
                if (timeToChangeSharedFrame()) {
                    lastSharedFrameChange = System.currentTimeMillis();
                    incrementAnimIndex();
                    try {
                        shared.get(sharedKey).forEach(anim -> {
                            anim.animIndex = animIndex;
                        });
                    } catch (ConcurrentModificationException e) {
                        System.out.println(e);
                    }
                }
            }
        }

        BufferedImage frame = getCurrentFrame();
        if (rotationOriginX != null && rotationOriginY != null) {
            // todo, multiple could probably be less than 2
            BufferedImage rotated = new BufferedImage(Math.max(frame.getWidth(), frame.getHeight()) * 2, Math.max(frame.getWidth(), frame.getHeight()) * 2, frame.getType());
            Graphics2D graphic = rotated.createGraphics();
            graphic.setBackground(new Color(255, 255, 255, 150));
//            graphic.clearRect(0, 0, rotated.getWidth(), rotated.getHeight());
            graphic.translate(rotated.getWidth() / 2 - rotationOriginX, rotated.getHeight() / 2 - rotationOriginY);
            graphic.rotate(Math.toRadians(angle), rotationOriginX, rotationOriginY);
            graphic.drawImage(frame, null, 0, 0);
            graphic.dispose();
            frame = rotated;
            //x -= rotated.getWidth() / 2;
            //y -= rotated.getHeight() / 2;
        }

        if (zoomable) {
            double zoom = Camera.getInstance().getZoom();
            g.drawImage(frame, x, y, (int) (frame.getWidth() * scaleX * zoom), (int) (frame.getHeight() * scaleY * zoom), null);
        } else {
            g.drawImage(frame, x, y, (int) (frame.getWidth() * scaleX), (int) (frame.getHeight() * scaleY), null);
        }
    }

    private void incrementAnimIndex() {
        if (reverse) {
            animIndex--;
            if (animIndex < 0) {
                animIndex = animations.size() - 1;
            }
        } else {
            animIndex = (animIndex + 1) % animations.size();
        }
    }

    public BufferedImage getCurrentFrame() {
        return animations.get(animIndex);
    }

}
