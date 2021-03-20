package animation;

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
    private int frames;
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
                    graphic.rotate(Math.toRadians(90), width/2, height/2);
                    graphic.drawImage(bufferedImage, null, 0, 0);
                    graphic.dispose();
                    bufferedImage = rotated;
                }

                ImageBank.images.put(fileNameAtAngle, bufferedImage);
            }

            int frameHeight = height / frames;

            for(int i = 0; i < frames; i++) {
                BufferedImage subImage = bufferedImage.getSubimage(0, i * frameHeight, width, frameHeight);

//                for (int y = 0; y < subImage.getHeight(); y += 10) {
//                    for (int x = 0; x < subImage.getWidth(); x += 10) {
//                        int pixel = subImage.getRGB(x, y);
//                        if( (pixel>>24) != 0x00 ) {
//                            subImage.setRGB(x, y, 0 );
//                        }
//                    }
//                }

//                Graphics graphics = subImage.getGraphics();
//                graphics.setColor(new Color(0, 0, 0, 2));
//                graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

                animations.add(subImage);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Image could not be read");
        }
    }

    public boolean timeToChangeFrame() {
        // convert time difference to seconds
        return System.currentTimeMillis() - lastFrameChange >= delay * 1000;
    }

    public boolean timeToChangeSharedFrame() {
        // convert time difference to seconds
        return System.currentTimeMillis() - lastSharedFrameChange >= delay * 1000;
    }

    public void draw(Graphics g, int x, int y) {
        draw(g, x, y, 0);
    }

    public void draw(Graphics g, int x, int y, Integer angle) {
        x += xOffset;
        y += yOffset;

        // draw, may want to pass graphics here
        // or have graphics as static
        if (frames > 1) {
            if (sharedKey == null) {
                if (timeToChangeFrame()) {
                    lastFrameChange = System.currentTimeMillis();
                    animIndex = (animIndex + 1) % animations.size();
                }
            } else {
                if (timeToChangeSharedFrame()) {
                    lastSharedFrameChange = System.currentTimeMillis();
                    animIndex = (animIndex + 1) % animations.size();
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

        if (zoomable) {
            double zoom = Camera.getInstance().getZoom();
            g.drawImage(getCurrentFrame(), x, y, (int) (getCurrentFrame().getWidth() * scaleX * zoom), (int) (getCurrentFrame().getHeight() * scaleY * zoom), null);
        } else {
            g.drawImage(getCurrentFrame(), x, y, (int) (getCurrentFrame().getWidth() * scaleX), (int) (getCurrentFrame().getHeight() * scaleY), null);
        }
    }

    public BufferedImage getCurrentFrame() {
        return animations.get(animIndex);
    }

}
