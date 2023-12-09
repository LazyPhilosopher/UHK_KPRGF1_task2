package rasterize;

import struct.Point;

import java.awt.*;
import java.util.List;

public class LineRasterizer extends CommonRasterizer{

    public LineRasterizer(Raster raster){
        super(raster);
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        java.util.List<struct.Point> points = getLinePoints(x1, y1, x2, y2);
        for (struct.Point point : points) {
            this.raster.setPixel(point.X(), point.Y(), color.getRGB());
        }
    }

    public void drawDottedLine(int dot_size, int x1, int y1, int x2, int y2, Color color) {
        List<struct.Point> points = getLinePoints(x1, y1, x2, y2);
        int step = 0;
        for (Point point : points) {
            step += 1;
            if (step % (2*dot_size) < dot_size){
                continue;
            }
            this.raster.setPixel(point.X(), point.Y(), color.getRGB());

        }
    }

}
