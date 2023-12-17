package rasterize.struct;

import rasterize.RasterizerUtils;
import rasterize.Raster;
import struct.Line;
import struct.Point;

import java.awt.*;
import java.util.List;


public class LineRasterizer extends RasterizerUtils {

    public LineRasterizer(Raster raster){
        super(raster);
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        for (Point point : getLinePoints(x1, y1, x2, y2)) {
            this.raster.setPixel(point.X(), point.Y(), color.getRGB());
        }
    }

    public void drawDottedLine(int dot_size, int x1, int y1, int x2, int y2, Color color) {
        List<Point> points = getLinePoints(x1, y1, x2, y2);
        int step = 0;
        for (Point point : points) {
            step += 1;
            if (step % (2*dot_size) < dot_size){
                continue;
            }
            this.raster.setPixel(point.X(), point.Y(), color.getRGB());

        }
    }

    public void drawDottedLine(int dot_size, Point a, Point b, Color color) {
        drawDottedLine(dot_size, a.X(), a.Y(), b.X(), b.Y(), color);
    }

    public List<Point> getLinePoints(Line line){
        return getLinePoints(line.start_point().X(), line.start_point().Y(), line.end_point().X(), line.end_point().Y());
    }
}
