package gui;

import rasterize.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;


public class Panel extends JPanel {

    private RasterBufferedImage raster;

    public RasterBufferedImage getRaster() {
        return raster;
    }

    public void setImg (BufferedImage new_img) {
        raster.setImg(new_img);
    }

    private static final int FPS = 5;
    public static final int WIDTH = 800, HEIGHT = 600;

    Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        raster = new RasterBufferedImage(WIDTH, HEIGHT);
        raster.setClearColor(Color.BLACK.getRGB());
        setLoop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
        // pro zájemce - co dělá observer - https://stackoverflow.com/a/1684476
    }

    public void resize(){
        if (this.getWidth()<1 || this.getHeight()<1)
            return;
        if (this.getWidth()<=raster.getWidth() && this.getHeight()<=raster.getHeight()) //no resize if new is smaller
            return;
        RasterBufferedImage newRaster = new RasterBufferedImage(this.getWidth(), this.getHeight());

        newRaster.draw(raster);
        raster = newRaster;
    }

    private void setLoop() {
        // časovač, který N krát za vteřinu obnoví obsah plátna aktuálním img
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, FPS);
    }

    public void clear() {
        raster.clear();
    }

    public void writeText(String text, int font_size, int x, int y, int color){
        raster.writeText(text, font_size, x, y, color);
    }
}
