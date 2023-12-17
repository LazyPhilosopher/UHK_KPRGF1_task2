package rasterize.struct;

import rasterize.RasterizerUtils;
import rasterize.Raster;
import struct.Point;

public class PointRasterizer extends RasterizerUtils {

    public PointRasterizer(Raster raster){
        super(raster);
    }

    public void drawPoint(int x, int y, int int_color){
        for (int delta = -5; delta < 6; delta++){
            this.raster.setPixel(x+delta, y, int_color);
            this.raster.setPixel(x, y+delta, int_color);
        }
    }

    public void drawPoint(Point point, int int_color){
        this.drawPoint(point.X(), point.Y(), int_color);
    }
}
