package rasterize;

import java.awt.*;
import java.awt.image.BufferedImage;


public class RasterBufferedImage implements Raster {

    private BufferedImage img;
    private int color;

    public BufferedImage getImg() {
        return img;
    }
    
    public BufferedImage copyImg(){
        // Create a new BufferedImage of the same type and size
        BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        // Get the graphics context for the new image
        Graphics g = copy.createGraphics();

        // Draw the original image onto the new image
        g.drawImage(img, 0, 0, null);

        // Dispose the graphics context to release resources
        g.dispose();

        // Return the new image
        return copy;
    }
    
    public void setImg(BufferedImage new_img) {
        img = new_img;
    }

    public RasterBufferedImage(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void repaint(Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
    }

    public void writeText(String text, int font_size, int x, int y, int color){
        Graphics graphics = getGraphics();
        graphics.setFont(new Font("Arial", Font.PLAIN, font_size));
        graphics.setColor(new Color(color));
        graphics.drawString(text, x, y);
    }

    public void draw(RasterBufferedImage raster) {
        Graphics graphics = getGraphics();
        graphics.setColor(new Color(color));
        graphics.fillRect(0, 0, getWidth(), getHeight());
    }

    public Graphics getGraphics(){
        return img.getGraphics();
    }

    @Override
    public int getPixel(int x, int y) {
        return img.getRGB(x, y);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (x < 0 || x >= img.getWidth()){
            return;
        }
        if ( y< 0 || y >= img.getHeight()){
            return;
        }
        img.setRGB(x, y, color);
    }

    @Override
    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(new Color(color));
        g.clearRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
    }

    @Override
    public void setClearColor(int color) {
        this.color = color;
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

}
