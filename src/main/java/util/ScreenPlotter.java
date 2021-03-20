package util;

import animation.Animation;
import animation.AnimationBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import playground.Camera;
import playground.GameApp2;
import playground.ScreenInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class ScreenPlotter {
    static BufferedImage fullscreenImage = new BufferedImage(GameApp2.WIDTH, GameApp2.HEIGHT, BufferedImage.TYPE_INT_ARGB);
    static Animation plotAnimation = AnimationBuilder.getBuilder()
            .fileName("plot-blue.png")
            .xOffset(-15)
            .yOffset(-15)
            .zoomable(false)
            .build();

    public List<PlotPoint> plots = new ArrayList<>();

    public void draw(Graphics graphics, ScreenInfo screen, Camera camera) {
        Graphics2D imageGraphics = fullscreenImage.createGraphics();
        imageGraphics.setBackground(new Color(255, 255, 255, 0));
        imageGraphics.clearRect(0, 0, fullscreenImage.getWidth(), fullscreenImage.getHeight());
        plots.forEach(loc -> plotAnimation.draw(imageGraphics, loc.getLocation().getX(), loc.getLocation().getY()));
        imageGraphics.dispose();
        graphics.drawImage(fullscreenImage, 0, 0, fullscreenImage.getWidth(), fullscreenImage.getHeight(), null);
        cleanUpTimedOutPlots();
    }

    private void cleanUpTimedOutPlots() {
        Long now = System.currentTimeMillis();
        plots.stream()
             .filter(plot -> (plot.lifeTimeMillis != null && now > plot.timeCreated + plot.lifeTimeMillis) || plot.drawOnce)
             .collect(Collectors.toList())
             .stream()
             .forEach(plot -> unPlot(plot.getLocation().getX(), plot.getLocation().getY()));
    }

    private void plot(PlotPoint newPlot) {
        IntLoc loc = new IntLoc(newPlot.getLocation().getX(), newPlot.getLocation().getY());
        if (!plots.stream().anyMatch(plot -> plot.location.equals(loc))) {
            plots.add(newPlot);
        }
    }

    public void plot(int x, int y) {
        plot(new PlotPoint(new IntLoc(x, y)));
    }

    public void plot(int x, int y, Long plotLifeTimeMillis) {
        plot(new PlotPoint(new IntLoc(x, y), plotLifeTimeMillis));
    }

    public void plot(int x, int y, boolean drawOnce) {
        plot(new PlotPoint(new IntLoc(x, y), drawOnce));
    }

    public void unPlot(int x, int y) {
        plots = plots.stream().filter(plot -> !plot.location.equals(new IntLoc(x, y))).collect(Collectors.toList());
    }

    @Getter
    @Setter
    public class PlotPoint {
        IntLoc location;
        Long lifeTimeMillis;
        Long timeCreated;
        boolean drawOnce;

        public PlotPoint(IntLoc loc) {
            this(loc, null, false);
        }

        public PlotPoint(IntLoc loc, Long lifeTimeMillis) {
            this(loc, lifeTimeMillis, false);
        }

        public PlotPoint(IntLoc loc, boolean drawOnce) {
            this(loc, null, drawOnce);
        }

        public PlotPoint(IntLoc loc, Long lifeTimeMillis, boolean drawOnce) {
            this.lifeTimeMillis = lifeTimeMillis;
            this.location = loc;
            this.drawOnce = drawOnce;
            this.timeCreated = System.currentTimeMillis();
        }
    }


}
