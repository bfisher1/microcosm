package animation;

import lombok.Getter;
import lombok.Setter;
import player.Camera;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

@Getter
@Setter
public class Animation {
    private String filename;
    private List<BufferedImage> animations;
    private int scaleX;
    private int scaleY;
    private double delay;
    private long lastFrameChange;
    private int animIndex;
    private int frames;

    public Animation() {
        animations = new ArrayList<>();
        lastFrameChange = System.currentTimeMillis();
        animIndex = 0;
        frames = 1;
    }

    public void setFilename(String fileName) {
        this.filename = "src/main/resources/assets/textures/" + fileName;
    }


    public void load() {

        BufferedImage bufferedImage;
        try {
            int width, height;
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

            int frameHeight = height / frames;

            for(int i = 0; i < frames; i++) {
                animations.add(bufferedImage.getSubimage(0, i * frameHeight, width, frameHeight));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Image could not be read");
        }
    }

    public boolean timeToChangeFrame() {
        // convert time difference to seconds
        return System.currentTimeMillis() - lastFrameChange >= delay * 1000;
    }

    public void draw(Graphics g, int x, int y) {

        // draw, may want to pass graphics here
        // or have graphics as static
        if (frames > 1) {
            if ( timeToChangeFrame() ) {
                lastFrameChange = System.currentTimeMillis();
                animIndex = (animIndex + 1) % animations.size();
            }
        }
        g.drawImage(getCurrentFrame(), x, y, null);
    }

    public BufferedImage getCurrentFrame() {
        return animations.get(animIndex);
    }

}
